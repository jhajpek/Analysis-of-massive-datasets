# Analysis of massive datasets

This repository contains Java implementations for algorithms that handle massive datasets.

Some of the topics and algorithms in this repository are:

- Near duplicates search (SimHash, Locality-Sensitive Hashing)
- Finding frequent item sets (Park Chen Yu)
- Recommender systems (Collaborative Filtering)


## SimHash

SimHash is key algorithm in near duplicates search that maps high-dimensional vectors into small fingerprints.<br/>
Unlike traditional cryptographic hashes, SimHash ensures that similar items have similar hashes.

SimHash steps:
1. **Feature Extraction**: The document is split into features (e.g., words, tokens, k-shingles).
2. **Weighting**: Each feature is assigned a weight (e.g., using TF-IDF, simple frequency or assign weight of 1 for each feature).
3. **Hashing**: Each feature is hashed into a bitstring of length $f$ using a standard hash function.
4. **Vector Aggregation**: Initialize a vector $V$ of $f$ zeros. For each feature's hash: If the $i$-th bit is 1, add the feature's weight to $V[i]$, otherwise subtract the weight from $V[i]$.
5. **Fingerprint Creation**: For each element in $V$: If $V[i] >= 0$, $V[i] = 1$, otherwise $V[i] = 0$.

After you turn each document to a fingerprint, you can check which documents are similar with Hamming distance.<br/>
You can search for similar documents sequentially or with Locality-Sensitive Hashing.<br/>
Locality-Sensitive Hashing is used to sub-linearly identify near duplicate candidate pairs,<br/>
drastically reducing the computational complexity of similarity searches.

Locality-Sensitive Hashing steps:
1. Divide the $f$-bit fingerprint into $k$ bands.
2. If 2 fingerprints are equal in at least one band, they become candidates for similarity.
3. The bands are keys in a hash table to find candidates for similarity, reducing the search space from $O(N^2)$ to nearly $O(N)$.

Of course, there is also a negative effect of Locality-Sensitive Hashing and that is accuracy.<br/>
The width of the band can massively effect accuracy because if there are only a few long bands, some<br/>
documents can differ only in 1 bit in each band, but won't be considered as candidates for similarity.


### Implementation

Tokenization for feature extraction used in this repository was to split documents into words.<br/>
Each feature was assigned a weight of 1. Type of hash function can be changed through interface `HashFunction`.<br/>
The length of fingerprint is dependent on the number of bytes of the digest of each hash function.<br/>
Because of that, length of each band in Locality-Sensitive Hashing is 8.


### Input

Input from `System.in` of each version of search engine(`SequentialSearchEngine`, `LSHSearchEngine`) is:

N<br/>
first_document<br/>
...<br/>
last_document<br/>
Q<br/>
first_query<br/>
...<br/>
last_query<br/>

$N$ is the number of documents(`N >= 2`). In each of the next $N$ lines, there is a document of words<br/>
separated by whitespaces. $Q$ is the number of search queries(`Q > 0`). In each of the next $Q$ lines,<br/>
there is a query which are 2 integers $i$ and $k$ separated by a whitespace. $i$ is index of document(`0 <= i < N`)<br/>
and $k$ is the maximal Hamming distance(`0 <= k < 32`) that is allowed for two documents to be considered similar.<br/>
Make sure you use appropriate search engine depending on the value of $N$ from the input.


### Output

For each query, number of similar documents is printed out on `System.out` in a new line.



## PCY(Park Chen Yu)



## Collaborative filtering