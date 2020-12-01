#!/bin/bash

# localhost:9200/link_freq/_search
ELASTIC_HOST="localhost:9200"

index="link_freq"
num="10"
field=""
query=""

while getopts "i:f:n:q:" opt; do
  case "$opt" in
    i) index=$OPTARG;;
    n) num=$OPTARG;;
    f) field=$OPTARG;;
    q) query=$OPTARG;;
  esac
done

JSON_QUERY='{"query": {"match": {"'"$field"'": {"query": "'"$query"'"}}}}'

printf '%s\n' "$JSON_QUERY"

response=$(curl -XGET "${ELASTIC_HOST}/${index}/_search?size=${num}" -H"Content-Type: application/json" -d"${JSON_QUERY}")

field="link_name"
if [[ "$index" == "text_freq" ]]; then
  field="text_name"
fi

echo "$field | doc_freq |  col_freq | is_redirect"
echo "================="
echo $response | python3 -c \
"import sys, json; \
results=json.load(sys.stdin);\
hits=results['hits']['hits'];\
logs=\
[(result['_source']['$field']+' \
| '+result['_source']['doc_freq']+' \
| '+result['_source']['col_freq']+' \
| '+str(result['_source']['is_redirect'])) \
for result in hits];\
print(*logs, sep='\n')"
