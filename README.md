#Simple Chatting Application

Simple Chatting Application using Netty

##TLS Connection
For creating a secure communication channel between the server and client, you will need to generate a self-signed certificate or a valid certificate signed by
a trustworthy certificate authority.

This repository contains a sample self-signed certificate located under `src/resources` for verifying server identity.


| File      | Description       |
| ----------- | ----------- |
| wowchat-server.jks      | JKS Keystore (Server's certificate, private & public keys)|
| wowchat-server.cer      | Server certificate     |

_**Please note that this application only support TLS 1.2**_