
## Overview

KServe is a Kubernetes-based platform for serving machine learning models. In this phase, we set up the infrastructure required to deploy ML models locally on a MacBook.

### Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Kind Cluster                              │
│                                                                  │
│  ┌──────────────────┐  ┌──────────────────┐  ┌───────────────┐  │
│  │   cert-manager   │  │     KServe       │  │    Kourier    │  │
│  │                  │  │   Controller     │  │  (Networking) │  │
│  └──────────────────┘  └──────────────────┘  └───────────────┘  │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                 InferenceServices                         │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐       │   │
│  │  │   Model 1   │  │   Model 2   │  │   Model N   │       │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘       │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

# 1. Create cluster
kind create cluster --name <>

# 2. Install cert-manager
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.3/cert-manager.yaml
kubectl wait --for=condition=Available --timeout=300s deployment/cert-manager -n cert-manager
kubectl wait --for=condition=Available --timeout=300s deployment/cert-manager-webhook -n cert-manager
kubectl wait --for=condition=Available --timeout=300s deployment/cert-manager-cainjector -n cert-manager

# 3. Install KServe
kubectl apply -f https://github.com/kserve/kserve/releases/download/v0.12.1/kserve.yaml
kubectl wait --for=condition=Available --timeout=300s deployment/kserve-controller-manager -n kserve
kubectl apply -f https://github.com/kserve/kserve/releases/download/v0.12.1/kserve-cluster-resources.yaml

# 4. Configure for Kind
kubectl patch configmap/inferenceservice-config -n kserve --type=merge \
  -p '{"data":{"deploy":"{\"defaultDeploymentMode\": \"RawDeployment\"}"}}'

# 5. Verify
kubectl get pods -n cert-manager
kubectl get pods -n kserve

curl -X POST http://localhost:8081/reviews \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "PROD-001",
    "userId": "USER-123",
    "rating": 5,
    "title": "Excellent Product!",
    "content": "This product exceeded my expectations. Fast shipping and great quality. Highly recommend!"
  }'


  {
  "id": "6761a2b3c4d5e6f7a8b9c0d1",
  "productId": "PROD-001",
  "userId": "USER-123",
  "rating": 5,
  "title": "Excellent Product!",
  "content": "This product exceeded my expectations. Fast shipping and great quality. Highly recommend!",
  "sentiment": {
    "sentiment": "positive",
    "score": 0.95,
    "confidence": 0.9,
    "categories": ["product_quality", "shipping"],
    "summary": "Customer highly satisfied with product and delivery."
  },
  "createdAt": "2024-12-22T10:30:00Z"
}
