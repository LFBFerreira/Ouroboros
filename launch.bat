@ECHO OFF

cd Ouroboros\out\production\Ouroboros

::java -cp ".;.\libraries\*" luisf.ouroboros.Main -p -c ../../../src/luisf -m ../../../../models_output -g -v ../../../../graphics_output
::java -cp ".;.\libraries\*" luisf.ouroboros.Main -h -u https://github.com/LFBFerreira/Ouroboros
java -cp ".;.\libraries\*" luisf.ouroboros.Main -h


@pause