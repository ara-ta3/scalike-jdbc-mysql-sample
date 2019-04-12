SBT=./tools/sbt/bin/sbt
DOCKER=docker
MYSQL_PORT=3306
MYSQL_ROOT_PASSWORD=root
MYSQL_DOCKER_NAME=mysql_for_scalike_jdbc_sample
MYSQL_CONTAINER_ID=$(shell $(DOCKER) ps |grep $(MYSQL_DOCKER_NAME)|awk '{print $$1}')

run:
	$(SBT) run

sbt:
	$(SBT)

run/mysql/docker:
	$(DOCKER) run \
		--rm \
		--name $(MYSQL_DOCKER_NAME) \
		--detach \
		--publish $(MYSQL_PORT):3306 \
		--env MYSQL_ROOT_PASSWORD=$(MYSQL_ROOT_PASSWORD) \
		--env MYSQL_DATABASE=test \
		mysql:5.6

stop/mysql/docker:
	$(DOCKER) stop $(MYSQL_CONTAINER_ID)

logs/mysql/docker:
	$(DOCKER) logs $(MYSQL_CONTAINER_ID)
