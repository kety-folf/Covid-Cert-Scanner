# Covid-Cert-Scanner
a way to verify any New Zealand Covid vaccine certificate

# setup
to set this up you will need to have a Node JS server running on your phone (for security reasons i will not host an API)

1.install [Termux](https://f-droid.org/repo/com.termux_117.apk) this will download the latest version off f-droid 

2. update and install requied packages in Termux

```
 pkg update && pkg upgrade
```
```
pkg install nodejs && pkg install git
```

3. install the verifcation server from github [kety-folf/NZCP-express-server](https://github.com/kety-folf/NZCP-express-server)

```
git clone https://github.com/kety-folf/NZCP-express-server.git
```
```
cd NZCP-express-server
```

4. update node packages
```
npm i
```

5. start server

```
node index.js
```
you can now go back to the covid certificate scanner app and everything will work
