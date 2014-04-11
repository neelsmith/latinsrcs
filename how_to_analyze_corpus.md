# How to build fully indexed corpus with morphology

## Build index of all tokens ##

In editions dir:

    groovy ../groovy/indexTokens.groovy | tee ../indices/passageToToken.tsv


## Collect morphological analyses of all tokens ##

**First make a list of forms**.  In root dir of repository:

    cut -f2 indices/passageToToken.tsv | perl -pe 's/urn:cite:hclat:tokens.//' - | sort | uniq | tee uniqtokens.txt


**Analyze**

    groovy groovy/morphfull.groovy uniqtokens.txt  | tee morphanlaysis-all.txt

**Separate out detailed analyses from generic analyses**




    