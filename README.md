merkle-tree
===========

Merkle tree is used in distributed systems(and many other places) to detect differences between two large datasets by using minimal network transfers. More information is avaiable in wikipedia. http://en.wikipedia.org/wiki/Merkle_tree. 

Main principle behind merkle-tree is as shown below ![Image of merkle tree] (http://en.wikipedia.org/wiki/Merkle_tree#mediaviewer/File:Hash_Tree.svg)

1) Divide the data into blocks, and compute digest(hash) of each block. Assuming 

Merkle-tree assumptions

1) Network transfers are costlier than local computing.

[Amazon Dynamo] (http://www.allthingsdistributed.com/files/amazon-dynamo-sosp2007.pdf) has used that in its implementation.
There are even few [opensource] (http://www.ccnx.org/releases/ccnx-0.6.0/javasrc/src/org/ccnx/ccn/impl/security/crypto/MerkleTree.java) implementations available. 

Problem
========
In one of my projects, we replicate some users' da
