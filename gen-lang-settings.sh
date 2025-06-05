#! /usr/bin/env bash
dest=./src/chengfen/ft.clj

# download source json files
# They also have damn trailing commas in this file that you want to remove
# wget 'https://raw.githubusercontent.com/XAMPPRocky/tokei/refs/heads/master/languages.json'
# wget 'https://raw.githubusercontent.com/ozh/github-colors/refs/heads/master/colors.json'

if [ -f src/chengfen/ft.clj ]; then
    echo "Skipping ft.clj generation"
    exit 0
fi

# header
echo "(ns chengfen.ft)" >> $dest
echo "(require '[chengfen.utils :refer [hex-to-rgb]])" >> $dest
echo "(def fts {" >> $dest

# extension -> language mapping
jq -r '
  (.languages // .) as $data |
  [ $data | to_entries[] | 
    (.value.extensions) as $exts |
    if $exts then
      (if $exts|type == "array" then $exts[]
       elif $exts|type == "string" then $exts
       else empty end) as $ext |
      { ext: $ext, lang: (.value.name // .key) }
    else
      empty
    end
  ] | 
  group_by(.ext)[] | 
  .[0] | 
  "\"\(.ext)\" \"\(.lang)\""
' languages.json >> $dest

echo "})" >> $dest
echo "(def ft-color {" >> $dest

# language -> color mapping
jq -r 'to_entries[] | select(.value.color != null) | "\"\(.key)\" (hex-to-rgb \"\(.value.color)\")"' colors.json >> $dest
jq -r 'to_entries[] | select(.value.color != null) | "\"\(.key)\" (hex-to-rgb \"\(.value.color)\")"' more_colors.json >> $dest

echo "})" >> $dest
