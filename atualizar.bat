@echo off

del u:\siga\assistente\*.jar
del u:\siga\assistente\*.jnlp
copy /y d:\desenvolvimento\maven\assistente\doc\express\assistente\public\jws\assistente\assistente-win.jnlp u:\siga\assistente\assistente.jnlp
copy /y d:\desenvolvimento\maven\assistente\target\assistente*.jar u:\siga\assistente
xcopy /e/y d:\desenvolvimento\maven\assistente\target\lib u:\siga\assistente\lib

echo ------------------------------------------------------------
echo A T E N C A O
echo Alterar o arquivo assistente.jnlp no servidor!!!
echo ------------------------------------------------------------