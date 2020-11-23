# Kubernetes에 spring-petclinic-data-jdbc 샘플 어플리케이션 배포

이 프로젝트는 https://github.com/spring-petclinic/spring-petclinic-data-jdbc 에 구현된 샘플 어플리케이션을 Kubernetes 상에 배포하는 것을 목표로 하고 있습니다.
이 프로젝트를 Kubernetes 상에 배포하기 위해서는 다음의 작업이 추가적으로 필요합니다.
1. gradle 을 통한 어플리케이션 및 docker build
2. Kubernetes cluster 배포를 위한 metadata 수정
3. Kubernetes cluster 에 배포 
4. 정상 배포  

여기서 Kubernetes cluster 자체를 배포하는 과정은 생략하였으며, 배포 환경은 Docker Desktop 로 배포된 Kubernetes cluster v1.19.3 입니다.

## 1. gradle 을 통한 어플리케이션 및 docker build

다음 명령어를 수행해 build.gradle.kts 에 정의된 jib plugin 을 통해서 spring-petclinic-data-jdbc:2.3.0.RC1 이미지를 생성합니다.

```
./gradlew jibDockerBuild
```
jar 파일을 별도로 생성하고 싶다면 gradle build 명령어로 build/libs 하위에 jar 파일을 생성합니다. 

```
./gradlew build
```

## 2. Kubernetes cluster 배포를 위한 metadata 수정

* 1번 단계에서 빌드한 docker image 의 이미지명이 spring-petclinic-data-jdbc:2.3.0.RC1 으로 고정이므로, 이미지 명을 변경하고 싶다면 다음 중 하나의 방법을 사용하여 변경합니다.
  + build.gradle.kts 의 jib: to: image 하위 값 수정
  + 커맨드 수행
  ```
  docker tag spring-petclinic-data-jdbc:2.3.0.RC1 [새 이미지 명] 
  ```
* 이미지 명을 변경했다면, docker push를 수행한 후, 샘플 어플리케이션을 배포하기 위한 petclinic.yaml 파일의 spec: template: spec: containers: image 하위 값도 동일한 값으로 변경해 줍니다.
* mysql 의 StorageClassName 값은 manual로, 다른 StorageClass 를 사용하는 환경이라면 mysql.yaml 의 PersistentVolume 의 spec: storageClassName 하위 값을 변경해줍니다.
* 또한 mysql 의 host mount path 는 /tmp/mysql-pv 로, 다른 경로를 사용하고 싶다면, mysql.yaml 의 PersistentVolumeClaim 의 spec: hostPath: path: 하위 값을 변경해 줍니다.

## 3. Kubernetes cluster 에 배포

다음 명령어를 수행해 Kubernetes cluster 에 아래의 mysql 오브젝트를 배포합니다.
* Service
* PersistentVolume
* PersistentVolumeClaim
* Deployment

```
kubectl apply -f kubernetes/mysql.yaml
```

mysql 이 정상적으로 배포된 후 다음 명령어를 수행해 Kubernetes cluster 에 아래의 petclinic 샘플 어플리케이션의 오브젝트를 배포합니다.
* Service
* Deployment

```
kubectl apply -f kubernetes/petclinic.yaml
```

helm 을 이용하여 nginx-ingress 를 배포합니다.
참조 : [nginx-ingress-contoller 설치 가이드](https://kubernetes.github.io/ingress-nginx/deploy/#using-helm)

```
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm install my-release ingress-nginx/ingress-nginx
```

nginx-ingress-controller 를 통해 petclinic 샘플 어플리케이션을 접속하도록 ingress 오브젝트를 배포합니다.

```
kubectl apply -f kubernetes/ingress.yaml
```

## 4. 정상 배포 확인

배포 이후 nginx-ingress-controller 의 endpoint/ 로 접속해 어플리케이션 정상 기동을 확인합니다.

**샘플 어플리케이션의 context root 값은 / 로 고정이므로 ingress.yaml 의 spec: rules: http: paths: path: 하위 값을 변경하지 않고 반영하고 endpoint/ 로 접속합니다.**
