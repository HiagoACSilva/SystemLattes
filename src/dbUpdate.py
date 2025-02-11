import psycopg
import pandas as pd
import os
import numpy as np
import keys
import datetime
def updateDocente():
    folder = "data/csv/"
    archivesCSV = os.listdir(folder)
    with psycopg.connect(keys.link_connection_database()) as conn:

        # for archive in archivesCSV:
            df = pd.read_csv(f"{folder}{archivesCSV[3]}")
            for _, row in df.iterrows():
                name = row['Nome']
                lattes = row['Lattes']
                data_atualizacao = datetime.date.today().strftime('%d%m%Y')
                with conn.cursor() as cur:

                    #ESTAVA ACONTECENDO UM ERRO EM QUE ALGUNS LATTES ESTAVAM INDO PARA O BANCO DE DADOS COMO FLOATS
                    #ESSA CONDIÇÃO SERVE APENAS PARA CONVERTER DE FLOAT PARA INT PARA RETIRAR OS .0
                    if type(lattes) is float and not np.isnan(lattes):
                        lattes = int(lattes)

                    #ISSO SERIA PARA CONVERTER O INT EM STR PARA FUNCIONAR A BUSCA E PEGAR O RESULTADO PARA USAR DEPOIS
                    lattes = str(lattes)
                    cur.execute("SELECT 1 FROM public.docente WHERE id_lattes = %s\
                                ",(lattes,))
                    resultlattesDocente = cur.fetchone()

                    #AQUI SERIA PARA VER SE JA EXISTE UM NOME IGUAL
                    cur.execute("SELECT 1 FROM public.docente WHERE nome = %s\
                                ",(name,))
                    resultnameDocente = cur.fetchone()

                    if(resultlattesDocente is not None and lattes != 'nan'): #LATTES EXISTE NO BD(S), LATTES NULO(N), PASSA PRO PROXIMO
                        print(f"{name} ja existe") 
                        continue

                    elif resultnameDocente is not None and lattes != 'nan': #NOME EXISTE NO BD(S), LATTES NULO(N), UPDATE
                        #MAS SO SE O REGISTRO ACHADO TIVER O LATTES NULO
                        cur.execute("UPDATE public.docente \
                                    SET id_lattes = %s, \
                                    data_atualizacao = %s\
                                    WHERE nome = %s\
                                    AND EXISTS(SELECT 1 \
                                    FROM public.docente\
                                    WHERE nome = %s\
                                    AND id_lattes IS NULL)",(lattes, data_atualizacao, name, name))
                        conn.commit()
                        print(f"{name} Atualizado")

                    elif(resultnameDocente is not None and lattes == 'nan'): #NOME EXISTE NO BD(S), LATTES NULO(S), PASSA PRO PROXIMO
                        print(f"{name} Pulado")
                        continue

                    elif lattes == 'nan': #NOME EXISTE NO BD(N), LATTES NULO(S), ADICIONA NOME COM LATTES NULO
                        cur.execute("INSERT INTO public.docente(nome, id_lattes, data_atualizacao) VALUES(%s, %s, %s)\
                                    ", (name, None, data_atualizacao))
                        conn.commit()
                        print(f"{name} inserido sem lattes")

                    elif resultlattesDocente is None: #NOME EXISTE NO BD(N), LATTES NULO(N), ADICIONA NOME E LATTES
                        cur.execute("INSERT INTO public.docente(nome, id_lattes, data_atualizacao) VALUES(%s, %s, %s)\
                                    ", (name, lattes, data_atualizacao))
                        conn.commit()
                        print(f"{name} inserido")
                cur.close()
                    #ESSES PRINTS SO SÃO PARA TER UM CONTROLEZINHO BASICO DO QUE TA ACONTECENDO(PREGUIÇA DE USAR O DEBUG)


def updateDiscente():
    folder = "data/csv/"
    archivesCSV = os.listdir(folder)
    with psycopg.connect(keys.link_connection_database()) as conn:

        # for archive in archivesCSV:
            df = pd.read_csv(f"{folder}{archivesCSV[2]}")
            for _, row in df.iterrows():
                name = row['Nome']
                lattes = row['Lattes']
                cpf = str(row['CPF'])
                #ALGUNS CPFS TINHAM MENOS NUMEROS QUE O PADRAO, DESCONFIO QUE SEJA PELO FATO DE QUE VIRARAM INTEIROS 
                #FAZENDO COM QUE OS ZEROS A ESQUERDA FOSSEM IGNORADOS, zfill ADICIONA OS ZEROS A ESQUERDA PARA COMPLETAR O NUMERO
                if len(cpf) != 11:
                    cpf = cpf.zfill(11)
                data_atualizacao = datetime.date.today().strftime('%d%m%Y')
                with conn.cursor() as cur:

                    #ESTAVA ACONTECENDO UM ERRO EM QUE ALGUNS LATTES ESTAVAM INDO PARA O BANCO DE DADOS COMO FLOATS
                    #ESSA CONDIÇÃO SERVE APENAS PARA CONVERTER DE FLOAT PARA INT PARA RETIRAR OS .0
                    if type(lattes) is float and not np.isnan(lattes):
                        lattes = int(lattes)

                    #ISSO SERIA PARA CONVERTER O INT EM STR PARA FUNCIONAR A BUSCA E PEGAR O RESULTADO PARA USAR DEPOIS
                    lattes = str(lattes)
                    cur.execute("SELECT 1 FROM public.discente WHERE id_lattes = %s\
                                ",(lattes,))
                    resultlattes = cur.fetchone()

                    #AQUI SERIA PARA VER SE JA EXISTE UM NOME IGUAL
                    cur.execute("SELECT 1 FROM public.discente WHERE nome = %s\
                                ",(name,))
                    resultname = cur.fetchone()

                    if(resultlattes is not None and lattes != 'nan'): #LATTES EXISTE NO BD(S), LATTES NULO(N), PASSA PRO PROXIMO
                        print(f"{name} ja existe") 
                        continue

                    elif resultname is not None and lattes != 'nan': #NOME EXISTE NO BD(S), LATTES NULO(N), UPDATE
                        #MAS SO SE O REGISTRO ACHADO TIVER O LATTES NULO
                        cur.execute("UPDATE public.discente \
                                    SET id_lattes = %s, \
                                    data_atualizacao = %s,\
                                    cpf = %s\
                                    WHERE nome = %s\
                                    AND EXISTS(SELECT 1 \
                                    FROM public.discente\
                                    WHERE nome = %s\
                                    AND id_lattes IS NULL)",(lattes, data_atualizacao, cpf, name, name))
                        conn.commit()
                        print(f"{name} Atualizado")

                    elif(resultname is not None and lattes == 'nan'): #NOME EXISTE NO BD(S), LATTES NULO(S), PASSA PRO PROXIMO
                        print(f"{name} Pulado")
                        continue

                    elif lattes == 'nan': #NOME EXISTE NO BD(N), LATTES NULO(S), ADICIONA NOME COM LATTES NULO
                        cur.execute("INSERT INTO public.discente(nome, id_lattes, data_atualizacao, cpf) VALUES(%s, %s, %s, %s)\
                                    ", (name, None, data_atualizacao, cpf))
                        conn.commit()
                        print(f"{name} inserido sem lattes")

                    elif resultlattes is None: #NOME EXISTE NO BD(N), LATTES NULO(N), ADICIONA NOME E LATTES
                        cur.execute("INSERT INTO public.discente(nome, id_lattes, data_atualizacao, cpf) VALUES(%s, %s, %s, %s)\
                                    ", (name, lattes, data_atualizacao, cpf))
                        conn.commit()
                        print(f"{name} inserido")
                cur.close()
                    #ESSES PRINTS SO SÃO PARA TER UM CONTROLEZINHO BASICO DO QUE TA ACONTECENDO(PREGUIÇA DE USAR O DEBUG)

def refreshLattesDiscenteByCpf(folder):
    archivesCSV = folder
    data_atualizacao = datetime.date.today().strftime('%d%m%Y')
    with psycopg.connect(keys.link_connection_database()) as conn:
        # for archive in archivesCSV:
            df = pd.read_csv(f"{folder}{archivesCSV[0]}")
            for _, row in df.iterrows():
                lattes = row['lattes']
                cpf = str(row['cpf']).zfill(11)
                with conn.cursor() as cur:
                    cur.execute("UPDATE public.discente \
                                    SET id_lattes = %s, \
                                    data_atualizacao = %s\
                                    WHERE cpf = %s\
                                    AND EXISTS(SELECT 1 \
                                    FROM public.discente\
                                    WHERE cpf = %s\
                                    AND id_lattes IS NULL)",(lattes, data_atualizacao, cpf, cpf))
                    conn.commit()
                    print(f"cpf:{cpf} atualizado com o lattes:{lattes}")
                    cur.close()
    conn.close()

def refreshLattesDocenteByCpf(folder):
    data_atualizacao = datetime.date.today().strftime('%d%m%Y')
    with psycopg.connect(keys.link_connection_database()) as conn:
        # for archive in archivesCSV:
            df = pd.read_csv(f"{folder}")
            for _, row in df.iterrows():
                lattes = row['lattes']
                cpf = str(row['cpf']).zfill(11)
                with conn.cursor() as cur:
                    cur.execute("UPDATE public.docente \
                                    SET id_lattes = %s, \
                                    data_atualizacao = %s\
                                    WHERE cpf = %s\
                                    AND EXISTS(SELECT 1 \
                                    FROM public.docente\
                                    WHERE cpf = %s\
                                    AND id_lattes IS NULL)",(lattes, data_atualizacao, cpf, cpf))
                    conn.commit()
                    print(f"cpf:{cpf} atualizado com o lattes:{lattes}")
                    cur.close()
    conn.close()



#refreshLattesDiscenteByCpf()
#updateDiscente()
#updateDocente()