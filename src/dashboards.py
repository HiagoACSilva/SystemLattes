from sqlalchemy import create_engine
import psycopg
import pandas as pd
import dash
from dash import dcc, html
from dash.dependencies import Input, Output
import plotly.express as px

engine = create_engine("postgresql+psycopg://postgres.ggddtpuxsvhuktrevnuk:oZuYmkd9pUxRNTUY@aws-0-sa-east-1.pooler.supabase.com:5432/ufmadb")

queries = {
    "tipo": "SELECT tipo, COUNT(*) AS total_producoes FROM public.producao WHERE ano BETWEEN 2021 AND 2024 GROUP BY tipo ORDER BY total_producoes DESC",
    "tipo_ano": "SELECT ano, tipo, COUNT(*) AS total_producoes FROM public.producao WHERE ano BETWEEN 2021 AND 2024 GROUP BY ano, tipo ORDER BY ano DESC",
    "qualis": "SELECT qualis, COUNT(*) AS total_producoes FROM public.producao WHERE ano BETWEEN 2021 AND 2024 GROUP BY qualis ORDER BY total_producoes DESC",
    "qualis_ano": "SELECT ano, qualis, COUNT(*) AS total_producoes FROM public.producao WHERE ano BETWEEN 2021 AND 2024 GROUP BY ano, qualis ORDER BY ano DESC"
}

df_tipo = pd.read_sql(queries["tipo"], engine)
df_tipo_ano = pd.read_sql(queries["tipo_ano"], engine)
df_qualis = pd.read_sql(queries["qualis"], engine)
df_qualis_ano = pd.read_sql(queries["qualis_ano"], engine)


app = dash.Dash(__name__)

app.layout = html.Div([
    html.H1("Análise de Produções"),
    
    dcc.Tabs([
        dcc.Tab(label="Produções por Tipo", children=[
            dcc.Graph(figure=px.pie(df_tipo, names="tipo", values="total_producoes", title="Produções por Tipo", hole=0.5))
        ]),
        
        dcc.Tab(label="Produções por Tipo por Ano", children=[
            dcc.Graph(figure=px.bar(df_tipo_ano, x="ano", y="total_producoes", color="tipo", title="Produções por Tipo por Ano", text='total_producoes'))
        ]),
        
        dcc.Tab(label="Produções por Qualis", children=[
            dcc.Graph(figure=px.bar(df_qualis, x="qualis", y="total_producoes", title="Produções por Qualis"))
        ]),
        
        dcc.Tab(label="Produções por Qualis por Ano", children=[
            dcc.Graph(figure=px.line(df_qualis_ano, x="ano", y="total_producoes", color="qualis", title="Produções por Qualis por Ano")
                      .update_layout(
                            xaxis=dict(
                            tickmode='linear',    # Define o modo de ticks como linear
                            tick0=2021,           # Define o primeiro valor do eixo X
                            dtick=1               # Define a distância entre os ticks (1 ano)
                            )
                        )
                    )
        ])
    ])
])

if __name__ == "__main__":
    app.run_server(debug=True)
