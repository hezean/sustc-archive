import streamlit as st
import pandas as pd
import pandas.io.json as pjson
import json
from datetime import datetime, timedelta
import pydeck as pdk


# st.set_page_config(layout="wide")


def basic_analyze(city):
    raw = pjson.loads(open(f'data/{city}.json').read())
    raw = pd.json_normalize(raw, record_path='data')
    raw['dateId'] = pd.to_datetime(raw['dateId'], format='%Y%m%d')
    raw = raw.set_index(['dateId'])
    return raw


def time_agg(raw: pd.DataFrame, freq, only22):
    if only22:
        raw = raw[raw.index >= pd.to_datetime('20220210', format='%Y%m%d')]
    return raw.groupby(pd.Grouper(freq=freq[0])).sum()


def sum_city(city, tt):
    df = time_agg(basic_analyze(city), 'M', False)
    df = df.loc[str(tt)[:7]]
    return df.iloc[0]['confirmedCount']


"""
## Comparison of Epidemic Development in Cities with Different Economic Status
> Sample Cities: Guangdong, Shanghai, Jilin, Yunnan
"""

rng_sel = st.select_slider(
    'Data aggregate by',
    options=['Day', 'Month', 'Year'])

col1, col2 = st.columns(2)

with col1:
    only2022 = st.checkbox('Recent Erupt Only')

with col2:
    cnt_or_rate = st.checkbox('Count / Increase')

gd_raw = time_agg(basic_analyze('广东')['confirmedIncr' if cnt_or_rate else 'confirmedCount'], rng_sel, only2022)
sh_raw = time_agg(basic_analyze('上海')['confirmedIncr' if cnt_or_rate else 'confirmedCount'], rng_sel, only2022)
jl_raw = time_agg(basic_analyze('吉林')['confirmedIncr' if cnt_or_rate else 'confirmedCount'], rng_sel, only2022)
yn_raw = time_agg(basic_analyze('云南')['confirmedIncr' if cnt_or_rate else 'confirmedCount'], rng_sel, only2022)

merge = pd.merge(gd_raw, sh_raw, how='inner', left_index=True, right_index=True)
merge = pd.merge(merge, jl_raw, how='inner', left_index=True, right_index=True)
merge = pd.merge(merge, yn_raw, how='inner', left_index=True, right_index=True)
merge.columns = ['Guangdong', 'Shanghai', 'Jilin', 'Yunnan']

st.line_chart(merge)

if cnt_or_rate and only2022:
    gd_tmp = time_agg(basic_analyze('广东'), rng_sel, only2022)
    sh_tmp = time_agg(basic_analyze('上海'), rng_sel, only2022)
    jl_tmp = time_agg(basic_analyze('吉林'), rng_sel, only2022)
    yn_tmp = time_agg(basic_analyze('云南'), rng_sel, only2022)

    gd_tmp['rate'] = gd_tmp['confirmedCount'] / (gd_tmp['confirmedCount'] - gd_tmp['confirmedIncr'])
    sh_tmp['rate'] = sh_tmp['confirmedCount'] / (sh_tmp['confirmedCount'] - sh_tmp['confirmedIncr'])
    jl_tmp['rate'] = jl_tmp['confirmedCount'] / (jl_tmp['confirmedCount'] - jl_tmp['confirmedIncr'])
    yn_tmp['rate'] = yn_tmp['confirmedCount'] / (yn_tmp['confirmedCount'] - yn_tmp['confirmedIncr'])

    incr = pd.merge(gd_tmp['rate'], sh_tmp['rate'], how='inner', left_index=True, right_index=True)
    incr = pd.merge(incr, jl_tmp['rate'], how='inner', left_index=True, right_index=True)
    incr = pd.merge(incr, yn_tmp['rate'], how='inner', left_index=True, right_index=True)
    incr.columns = ['Guangdong_incr', 'Shanghai_incr', 'Jilin_incr', 'Yunnan_incr']

    st.line_chart(incr)

to_time = st.slider(
    "From start to",
    value=datetime(2022, 2, 1),
    min_value=datetime(2020, 2, 1),
    max_value=datetime(2022, 4, 30),
    step=timedelta(days=30),
    format="YYYY/MM")

cities = pd.DataFrame()

raw_geo = json.loads(open('china.json').read())
cities['name'] = [r['properties']['name'] for r in raw_geo]
cities['lat'] = [r['properties']['cp'][1] for r in raw_geo]  # N
cities['lon'] = [r['properties']['cp'][0] for r in raw_geo]  # E
cities['den'] = [r['properties']['den'] for r in raw_geo]  # E
cities['pop'] = [r['properties']['population'] for r in raw_geo]  # E
cities['num'] = [sum_city(r, to_time) for r in cities['name']]

"""
#### Cumulated Confirmed Number
"""

st.pydeck_chart(pdk.Deck(
    map_style='mapbox://styles/mapbox/light-v9',
    initial_view_state=pdk.ViewState(
        latitude=30.19,
        longitude=102.91,
        zoom=3,
        pitch=50,
    ),
    layers=[
        pdk.Layer(
            'ColumnLayer',
            data=cities,
            get_position='[lon, lat]',
            get_elevation='num',
            radius=120000,
            elevation_scale=4,
            elevation_range=[0, 10000000],
            get_fill_color=["num", "lon", "lat", 150],
            pickable=True,
        ),
    ],
))

cities_div_population = cities.copy()
cities_div_population['num_pop'] = cities_div_population['num'] / cities_div_population['pop']

st.pydeck_chart(pdk.Deck(
    map_style='mapbox://styles/mapbox/light-v9',
    initial_view_state=pdk.ViewState(
        latitude=30.19,
        longitude=102.91,
        zoom=3,
        pitch=50,
    ),
    layers=[
        pdk.Layer(
            'ColumnLayer',
            data=cities_div_population,
            get_position='[lon, lat]',
            get_elevation='num_pop',
            radius=120000,
            elevation_scale=500000000,
            elevation_range=[0, 1],
            get_fill_color=["num_pop * 100000", "lon", "lat", 130],
            pickable=True,
        ),
    ],
))

cities_div_den = cities.copy()
cities_div_den['num_den'] = cities_div_population['num'] / cities_div_population['den']

st.pydeck_chart(pdk.Deck(
    map_style='mapbox://styles/mapbox/light-v9',
    initial_view_state=pdk.ViewState(
        latitude=30.19,
        longitude=102.91,
        zoom=3,
        pitch=50,
    ),
    layers=[
        pdk.Layer(
            'ColumnLayer',
            data=cities_div_den,
            get_position='[lon, lat]',
            get_elevation='num_den',
            radius=120000,
            elevation_scale=2500,
            elevation_range=[0, 100000],
            get_fill_color=["num_den * 100000", "lon", "lat", 130],
            pickable=True,
        ),
    ],
))

disp = cities_div_population.copy()
disp['Infection'] = cities['num']
disp.drop(['lat', 'lon'], axis=1, inplace=True)

disp = disp.reindex(columns=['name', 'Infection', 'num', 'population'])
disp.rename(columns={'name': 'City', 'num': 'Infection Rate', 'population': 'Population'}, inplace=True)
disp.sort_values(by=['Infection Rate'], inplace=True, ascending=False)
st.dataframe(disp)
