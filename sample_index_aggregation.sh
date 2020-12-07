curl --request GET \
  --url http://localhost:9200/link_freq/_search \
  --header 'Content-Type: application/json' \
  --data '{
  "query": {
    "match": { "link_name": "anarchism" }
  },
	"size": 0,
  "aggs": {
    "my_sample": {
      "sampler": {
        "shard_size": 100000
      },
      "aggs": {
        "keywords": {
          "significant_text": { "field": "link_name", "size": 5 }
        }
      }
    }
  }
}' | json_pp