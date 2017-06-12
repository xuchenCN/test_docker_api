import docker
import dockerpty
import sys

base_url = "unix://var/run/docker.sock"
api_version = "1.24"
client = docker.APIClient(base_url=base_url, version=api_version)
#container = client.containers.get("test_client")
container = client.containers()[0]

    

dockerpty.exec_command(client, container, "/bin/bash", True, sys.stdout, sys.stderr, sys.stdin)
