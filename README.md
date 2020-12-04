# Wiki Anchor Parser

## Prerequisities

- Docker
- docker-compose
- Java 11 (OpenJDK)
- Maven
    - `sudo apt-get install maven`

## Input

- `data` directory contains example XML input `enwiki-pages-sample.xml` (~10MB == 251 pages)
  which, after execution, will produce outputs in the same directory 
  (can be replaced by desired valid Wiki dump)

- `example_data` directory contains example output files and statistics from
  `7 578 382` wikipedia articles (~5GB `pages-parsed.txt`) , original wiki XML dump and
  `pages-parsed.txt` file are not inluded because of large size
  
## Running

In root directory run:

```bash
mvn compile exec:java -Dexec.mainClass="com.vif.Main"
```

Optionally (depending on machine), for processing large files you can increase some JVM parameters before running command above:
`export MAVEN_OPTS="-Xmx8192m -Xss2048m"`

### Indexing

Run `./index.sh` in order to index output .csv files. After few minutes, all files should
be successfully imported to Elasticsearch (localhost:9200) using Logstash (localhost:5000). 

In order to search indexed files, run: 
```bash
./search.sh -i <name-of-index> -f <field-name> -q <query> -n <num-of-result>
```

- `-i` index to search in (can be either `link_freq` or `text_freq`, default is `link_freq`)
- `-n` specifies number of results to be returned (default is `10`)
- `-f` specifies document field to match against (link_name/text_name, doc_freq, col_freq, is_redirect (only for links))
- `-q` specifies query that should be matched

Example:
```bash
./search.sh -i link_freq -f link_name -q anarchism -n 5
```

Shutting down containers: 
```
cd elk-docker && docker-compose down
```

# Results

## Input/Output data

1) Initial XML Wiki dump as input

    - `enwiki-latest-pages-articles.xml` (75GB)
  
2) after parsing of initial XML 

    - `sample.txt` - 16 388 705 pages (11GB)

3) after parsing of `sample.txt`

    - `link_freq.csv` - 19 675 581 unique anchor links (526MB)

    - `text_freq.csv` - 24 186 786 unique anchor texts (574MB)

    - `statistics.txt` - file containing anchor link and text statistics 
(avg. document and collection frequencies, redirect count, total numbers, maximums)

Optionally, you can sort .csv files using: 

```bash
LC_ALL=C sort -S 50% -t$'\t' -k2 -rn link_freq.csv > link_freq_sorted.csv
```

- `k2` - for document frequency
- `k3` - for collection frequency
