import requests
import pandas as pd
from bs4 import BeautifulSoup
from unidecode import unidecode
import keys

#SERIA BOM ADICIONAR UM TRATAMENTO DE ERRO DE CONEXAO, UM TIPO DE TRY CATCH EXCEPT

dadosAllDocentes = []

def webscraper(link, name, dados):
    dadosdf = []
    html = requests.get(link).content
    soup = BeautifulSoup(html, 'html.parser', from_encoding='utf-8') #PASSANDO O ENCODING PRA NAO DAR PROBLEMA COM CARACTERES ESPECIAIS

    #ESTOU PROCURANDO O LINK LATTES E O NOME, ENTAO TO PROCURANDO PELOS "tr" QUE SÃO ELEMENTOS DE TABELA
    for tr in soup.find_all('tr'):
        a_cor = tr.find('a', class_='cor') #PEGANDO NOME
        a_lattes = tr.find('a', id='endereco-lattes') # PEGANDO LATTES
        if a_cor:
            texto_cor = a_cor.text.strip().title()
        else:
            texto_cor = None
        if a_lattes:
            href_lattes = a_lattes['href']
            if "lattes" in href_lattes:
                href_lattes_splited = href_lattes.split("/") #UM FILTRO BASICO PRA PEGAR APENAS O NUMERO DO LATTES
                href_lattes = href_lattes_splited[-1] #COMO EU DISSE, APENAS O NUMERO LATTES
            else:
                href_lattes = None #MAS BEM, SE NAO TIVER O NUMERO ENTAO SO JOGO NULL
        else:
            href_lattes = None
        if a_cor != None: #SEM NOME, SEM REGISTRO, SIMPLES E DIRETO

            #AQUI SERIA UM TRATAMENTO BASICO DE DADOS
            #BASICAMENTE O QUE ISSO FAZ É: 
                #ESTOU RETIRANDO TODOS OS ACENTOS PORQUE ISSO VAI ATRAPALHAR NA HORA DE 
                #       ADICIONAR OS REGISTROS NO BANCO DE DADOS. DUPLICAÇÃO, ETC
            dupla = [(unidecode(texto_cor), href_lattes)] 

            #BASICAMENTE, OU MELHOR, LITERALMENTE, dados É UM VETOR DE DUPLA(nome, lattes)
            dadosdf += dupla

    dados += dadosdf #VETOR QUE PEGA TODOS OS REGISTROS, USADO PARA ARMAZENAR TUDO PARA APÓS CONVERTER NUM csv COM TODOS OS DOCENTES
    df = pd.DataFrame(dadosdf, columns=["Nome", "Lattes"])
    df.to_csv(f"data/csv/{name}.csv", index=False)


links = keys.links_ppg()
for link, name in links:
    webscraper(link, name, dadosAllDocentes)

df = pd.DataFrame(dadosAllDocentes, columns=["Nome", "Lattes"])
df = df.dropna(subset=['Lattes'])
df = df.drop_duplicates(subset=['Lattes'])
df.to_csv("data/csv/docentes.csv", index=False)
# def cleanCsv(folder):
#     csv = []
#     archives = os.listdir(folder)
#     for i in range(0, len(archives)):
#         df1 = pd.read_csv(f'{folder}{archives[i]}')
#         for j in range(i+1, len(archives)):
#             df2 = pd.read_csv(f'{folder}{archives[j]}')
#             csv+= compairCsv(df1, df2)
# def compairCsv(df1, df2):
#     csv = []
#     for k in range(0, len(df1.index)):
#         lineDf1 = df1.iloc[k]
#         linedf = [(lineDf1['nome'], None)]
#         for l in range(0, len(df2.index)):
#             lineDf2 = df2.iloc[l]
#             if(lineDf1['nome'] == lineDf2['nome']):
#                 if((lineDf1['lattes'] != None and lineDf2['lattes'] != None and lineDf1['lattes'] != lineDf2['lattes']) or (lineDf1['lattes'] != None)):
#                     linedf = [(lineDf1['nome'], lineDf1['lattes'])]
#                 elif(lineDf2['lattes'] != None):
#                     linedf = [(lineDf2['nome'],lineDf2['lattes'])]
    
#         csv += linedf
#     return csv
        
                
                



#link = input("url: ")
#name = input("name whitout .csv: ")