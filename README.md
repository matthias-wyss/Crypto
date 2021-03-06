# Crypto


Crypto is a small Java program that was created as part of a mini-project 1 during my first semester of my first year at EPFL.

It can encrypt or decrypt text following different algorithms.


### Installation

* Install Java 11 or higher
* Clone the repository

### How to use

* Open crypto/res/message.txt and write your message to encode
* Open crypto/res/key.txt and write your encoding key
* Open crypto/res/intForGeneratingPad.txt and write a positive integer
* Open crypto/res/spaceEncoding and write "true" or "false"
* Open a terminal
* Go to the crypto folder
* Write `java Crypto.java`and press enter
* See the results on crypto/res/results.txt, bruteForceCaesar.txt and bruteForceXor.txt

### Features

* Caesar
	* Encoding
	* Decoding with the key
	* Brute force decoding
	* Decoding with frequencies

* Vigenere
	* Encoding
	* Decoding with the key
	* Decoding with frequencies

* XOR
	* Encoding
	* Decoding with the key
	* Brute force decoding

* One Time Pad
	* Encoding
	* Decoding with the pad
	
* CBC
	* Encoding
	* Decoding with the pad

### Authors

* **Matthias Wyss**
* **Thibault Czarniak**

### License

This project is under MIT license - see [LICENSE.md](https://github.com/matthias-wyss/Crypto/blob/main/LICENSE.md) for more informations