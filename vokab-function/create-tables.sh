#! /bin/bash

aws dynamodb create-table \
   --table-name vokab-alias \
   --attribute-definitions AttributeName=alias,AttributeType=S AttributeName=conceptId,AttributeType=S \
   --key-schema AttributeName=alias,KeyType=HASH AttributeName=conceptId,KeyType=RANGE \
   --billing-mode PAY_PER_REQUEST \
   --endpoint-url http://localhost:8000


 aws dynamodb create-table \
    --table-name vokab-concept \
    --attribute-definitions AttributeName=uuid,AttributeType=S AttributeName=scheme,AttributeType=S AttributeName=label,AttributeType=S \
    --key-schema AttributeName=uuid,KeyType=HASH \
    --global-secondary-indexes \
      'IndexName=scheme-label-index,KeySchema=[{AttributeName=scheme,KeyType=HASH},{AttributeName=label,KeyType=RANGE}],Projection={ProjectionType=INCLUDE,NonKeyAttributes=[uuid]}' \
    --billing-mode PAY_PER_REQUEST \
    --endpoint-url http://localhost:8000

