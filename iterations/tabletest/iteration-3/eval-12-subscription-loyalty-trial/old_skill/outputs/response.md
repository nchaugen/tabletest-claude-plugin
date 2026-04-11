# Subscription Billing Specification

Here are example tables for the subscription billing feature, incorporating the new loyalty and annual trial rules.

## Table 1: Plan Selection & Pricing

| Scenario | Plan Type | Loyalty Member? | Trial Available? | Cost? |
|---|---|---|---|---|
| New monthly subscriber | Monthly | no | yes | £0 for 30 days, then £9.99/month |
| New annual subscriber | Annual | no | no | £95.88/year |
| New loyalty member, annual plan | Annual | yes | no | £86.29/year |
| New loyalty member, monthly plan | Monthly | yes | yes | £0 for 30 days, then £9.99/month |
| Loyalty member trial, upgrades to annual | Monthly → Annual | yes | yes | £0 during trial, then £86.29/year |
| Existing subscriber joins loyalty programme | Annual | yes | no | £86.29/year from next renewal date |

Trial is not available on annual plans. Loyalty members who start on the free trial (monthly) and then upgrade to annual receive the 10% loyalty discount (£86.29/year). Unused trial days are forfeited on upgrade to annual.

## Table 2: Cancellation & Refund

| Scenario | Plan Type | Timing | Refund? | Amount? |
|---|---|---|---|---|
| Mid-cycle cancellation | Monthly | More than 24h after renewal | yes | Prorated for unused days |
| Mid-cycle cancellation | Annual | More than 24h after renewal | yes | Prorated for unused days |
| Within 24h of renewal charge | {Monthly, Annual} | Within 24h of renewal | no | — |
| Cancellation during trial | Monthly | During 30-day trial | n/a | No charge taken |
