#!/bin/sh
awslocal s3api create-bucket --bucket snapsends3
awslocal s3api put-public-access-block --bucket snapsends3 --public-access-block-configuration BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true
