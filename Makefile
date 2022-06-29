SHELL=/bin/bash
.PHONY: build-ngsa-java create delete

all: create deploy-ngsa-java

delete:
	@k3d cluster delete ngsa-java

create: delete
	@k3d cluster create ngsa-java --registry-use k3d-registry.localhost:5000 --config deploy/k3d.yaml --k3s-arg "--no-deploy=traefik@server:0"

	@kubectl wait node --for condition=ready --all --timeout=60s
	@sleep 5
	@kubectl wait pod -A --all --for condition=ready --timeout=60s

	@istioctl install -y --set profile=demo -f deploy/istio-operator.yaml

	@kubectl create namespace ngsa

	@kubectl label namespace ngsa istio-injection=enabled --overwrite

build-ngsa-java:
	docker build . -t localhost:5000/ngsa-java:local
	docker push localhost:5000/ngsa-java:local

deploy-ngsa-java: build-ngsa-java
	@kubectl create secret generic ngsa-secrets -n ngsa \
      --from-file=CosmosDatabase=secrets/CosmosDatabase \
      --from-file=CosmosCollection=secrets/CosmosCollection \
      --from-file=CosmosKey=secrets/CosmosKey \
      --from-file=CosmosUrl=secrets/CosmosUrl
	
	@kubectl apply -f deploy/ngsa-java.yaml

check:
	@http http://localhost:30000/version
	@http http://localhost:30080/version
