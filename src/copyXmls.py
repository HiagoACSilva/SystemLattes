import paramiko
from scp import SCPClient

# Função para criar a conexão SSH
def create_ssh_client(hostname, port, username, password):
    client = paramiko.SSHClient()
    client.set_missing_host_key_policy(paramiko.AutoAddPolicy())  # Ignora verificação de chave de host
    client.connect(hostname, port, username, password)
    return client

# Função para copiar a pasta
def copy_folder_from_remote(ssh_client, remote_path, local_path):
    with SCPClient(ssh_client.get_transport()) as scp:
        scp.get(remote_path, local_path, recursive=True)

# Parâmetros de conexão
hostname = "200.137.132.131"  # IP ou hostname da máquina remota
port = 2022  # Porta SSH
username = "user"  # Seu usuário SSH
password = "EcIorKWONe"  # Sua senha SSH, se necessário

# Caminho da pasta remota e local
remote_path = "/home/user/importLattesPy/data/xmls"
local_path = "./data" 

# Conectar à máquina remota e copiar a pasta
ssh_client = create_ssh_client(hostname, port, username, password)
copy_folder_from_remote(ssh_client, remote_path, local_path)

print(f"Transferência da pasta {remote_path} concluída com sucesso!")
