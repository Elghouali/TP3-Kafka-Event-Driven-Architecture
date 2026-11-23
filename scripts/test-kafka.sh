#!/usr/bin/env bash
set -euo pipefail
# Kafka CLI inside the broker (works on Mac/Linux/Git Bash).
docker compose exec broker kafka-topics --bootstrap-server broker:29092 --create --if-not-exists --topic tp3-console --partitions 1 --replication-factor 1
echo 'Running Kafka consumer in background for a few seconds:'
(docker compose exec -T broker kafka-console-consumer --bootstrap-server broker:29092 --topic tp3-console --from-beginning --max-messages 1 &)
sleep 2
printf 'Bonjour Kafka depuis TP3\n' | docker compose exec -T broker kafka-console-producer --bootstrap-server broker:29092 --topic tp3-console
