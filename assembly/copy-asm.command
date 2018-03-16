cd `dirname $0`
rm *.asm
find ../../Code -name '*.asm' -exec cp {} ./ \;