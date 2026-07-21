# Eval Review — tabletest variant=qwen, Iteration 1

**Model:** qwen3.6:35b-mlx · **Grading:** claude-haiku-4-5 · **Date:** 2026-07-07 · **Evals:** 17

## Summary

0/93 (0.0%) · 0 tokens · 9106.3s

## Per-Eval Results

### ⚠️ Eval eval-18-convert-from-code

**0/21** · 0 tokens · 916337ms


### ⚠️ Eval eval-20-collections-and-quoting

**0/15** · 0 tokens · 2174565ms


### ⚠️ Eval eval-27-convert-from-testng

**0/19** · 0 tokens · 1118726ms


### ⚠️ Eval eval-28-convert-from-methodsource

**0/18** · 0 tokens · 2481429ms


### ⚠️ Eval eval-30-order-splitting-tt

**0/20** · 0 tokens · 2415286ms


## Variant vs Official (iterations 39 merged)

### Per-Eval Resource Comparison

| Eval | Pass (off) | Pass (var) | Tokens (off) | Tokens (var) | Tok Δ | Cost (off) | Cost (var) | Cost Δ | Time (off) | Time (var) | Time Δ |
|------|------------|------------|--------------|--------------|-------|------------|------------|--------|------------|------------|--------|
| eval-18-convert-from-code | 19/21 | 0/21 | 826034 | 0 | -100% | $0.5551 | $0.0000 | -100% | 148.2s | 916.3s | +518% |
| eval-20-collections-and-quoting | 15/15 | 0/15 | 1811829 | 0 | -100% | $1.1145 | $0.0000 | -100% | 301.8s | 2174.6s | +620% |
| eval-27-convert-from-testng | 17/19 | 0/19 | 1445041 | 0 | -100% | $1.0346 | $0.0000 | -100% | 337.7s | 1118.7s | +231% |
| eval-28-convert-from-methodsource | 16/18 | 0/18 | 986014 | 0 | -100% | $0.9027 | $0.0000 | -100% | 330.2s | 2481.4s | +651% |
| eval-30-order-splitting-tt | 19/20 | 0/20 | 3329150 | 0 | -100% | $2.2750 | $0.0000 | -100% | 664.9s | 2415.3s | +263% |
| **Totals (5 comparable)** | **86/93** | **0/93** | **8398068** | **0** | **-100%** | **$5.8819** | **$0.0000** | **-100%** | **1782.8s** | **9106.3s** | **+411%** |

**Comparable summary (5 evals in both):**

| Source | Pass Rate | Tokens | Cost | Time |
|--------|-----------|--------|------|------|
| qwen (iter 1) | 0/93 (0.0%) | 0 | $0.0000 | 9106.3s |
| official | 86/93 (92.5%) | 8398068 | $5.8819 | 1782.8s |
| **Δ** | | **-100%** | **-100%** | **+411%** |

### Per-Assertion Comparison

| Eval | Assertion | official | qwen |
|------|-----------|----------|------|

No load-bearing assertions found — variant matches official on all comparable assertions.

