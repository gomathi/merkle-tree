merkle-tree
===========

Merkle tree(Hash trees) is used in distributed systems(and many other places) to detect differences between two large datasets by using minimal network transfers. More information is avaiable in wikipedia. http://en.wikipedia.org/wiki/Merkle_tree. 

![Image of merkle tree] (http://upload.wikimedia.org/wikipedia/commons/9/95/Hash_Tree.svg)

Idea behind the merkle-tree is as following
===

1) Divide the data into blocks, and compute digest(hash) of each block. 
2) All hashes of these blocks become leaves of a tree. Using these leaves, we can build the complete tree upto the root.
3) If we have the merkle-trees of two similar datasets, that will help in detecting whether those datasets are same, or differing, by comparing only these digests, and we can quickly figure out which blocks are differing.

Merkle-tree assumptions
===

1) Network transfers are costlier than local computing. Hence digest calculation is done(even though it is a heavy process), and digest transferred through network, and that is used to detect inconsistencies, and it avoids unnecessary transfer entire block of data.

[Amazon Dynamo] (http://www.allthingsdistributed.com/files/amazon-dynamo-sosp2007.pdf) has used that in its implementation.
There are even few [opensource] (http://www.ccnx.org/releases/ccnx-0.6.0/javasrc/src/org/ccnx/ccn/impl/security/crypto/MerkleTree.java) implementations available. 

Problem
========
In one of my projects, we replicate some users' da
