#!/usr/bin/env bash
docker build -t swr.cn-east-2.myhuaweicloud.com/yb7/product-reports-libs:$1 .
docker push swr.cn-east-2.myhuaweicloud.com/yb7/product-reports-libs:$1
