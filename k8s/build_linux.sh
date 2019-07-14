#!/usr/bin/env bash
re="swr.cn-east-2.myhuaweicloud.com/yb7/([^:]+):([^ ]+)"
imageStr=$(kubectl --kubeconfig $KUBECONFIG_TEST get deploy product-reports -o jsonpath='{..image}')
echo "current version"
if [[ $imageStr =~ $re ]]; then echo ${BASH_REMATCH[2]}; fi


if [ $# -eq 0 ];
then nextVersion=$(./increment_version.sh -p ${BASH_REMATCH[2]});
else nextVersion=$(./increment_version.sh $1 ${BASH_REMATCH[2]});
fi

echo "next version"
echo $nextVersion;

git add .
git commit -m "product-reports:$nextVersion"
git push

rm -rf reports
unzip -q ../build/libs/reports-0.0.1.jar -x "BOOT-INF/lib/*" "org/**/*" "BOOT-INF/classes/fonts/*" -d reports
docker build -t swr.cn-east-2.myhuaweicloud.com/yb7/product-reports:$nextVersion .
docker push swr.cn-east-2.myhuaweicloud.com/yb7/product-reports:$nextVersion

kubectl --kubeconfig $KUBECONFIG_TEST set image deployment/product-reports product-reports=swr.cn-east-2.myhuaweicloud.com/yb7/product-reports:$nextVersion
kubectl --kubeconfig $KUBECONFIG_TEST get deploy product-reports  -o jsonpath='{..image}'

