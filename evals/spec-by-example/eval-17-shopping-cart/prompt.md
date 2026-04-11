We're building a shopping cart and I want to make sure we have good test coverage before we ship.

The basic flow is: customers browse a product catalogue, add items to their cart with a quantity, and can remove them again. Prices come from the catalogue, not from the customer — so the cart looks up what things cost. Customers can also enter a coupon code at any point. We support three kinds of coupons: percentage off the whole cart, a fixed amount off, or a discount on a specific product. Only one coupon can be active at a time though — if they enter a new code, it replaces whatever was there before. If the code is expired or doesn't exist, we show them an error message.

At checkout we verify stock levels. If anything in the cart doesn't have enough inventory, checkout fails and we tell them what's short. You also can't check out with an empty cart, obviously.

The cart total is the sum of quantity times price for each item, minus whatever coupon discount applies, and it can never go below zero — even if someone has a massive coupon on a small order.

We want every operation to return a user-facing message so the frontend can display it — things like 'Added 2x Widget to cart', 'Coupon has expired', 'Only 3 available' and so on.

Can you help us think through the test scenarios for this?
