import psycopg
import pandas as pd
import keys

def cpfDiscente():
    with psycopg.connect(keys.link_connection_database()) as conn:
        with conn.cursor() as cur:
            cur.execute("SELECT cpf FROM public.discente")
            coluna_como_lista = [row[0] for row in cur.fetchall()]
            df = pd.DataFrame(coluna_como_lista, columns=['cpf'])
            df.to_csv("data/csv/CPFsDiscente.csv", index=False)

def cpfDocente():
    with psycopg.connect(keys.link_connection_database()) as conn:
        with conn.cursor() as cur:
            cur.execute("SELECT cpf FROM public.docente")
            coluna_como_lista = [row[0] for row in cur.fetchall()]
            df = pd.DataFrame(coluna_como_lista, columns=['cpf'])
            df.to_csv("data/csv/CPFsDocente.csv", index=False)

def lattesDocente():
    with psycopg.connect(keys.link_connection_database()) as conn:
        with conn.cursor() as cur:
            cur.execute("SELECT id_lattes FROM public.docente")
            coluna_como_lista = [row[0] for row in cur.fetchall()]
            df = pd.DataFrame(coluna_como_lista, columns=['id_lattes'])
            df.to_csv("data/csv/lattesDocente.csv", index=False)
#cpfDiscente()