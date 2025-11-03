# This project
Was created as an empty demo project from start.spring.io Initializr

# File validation
I assume that the entire file should be rejected if only one line is incorrect.
Normally I'd discuss it with the team/product owner, but the strategy "all or nothing" is usually better and less confusing.

I am assuming I should validate the whole file because if one field is incorrect (for example instead of UUID we have ordinary strings) it might indicate that we got a wrong file, so it's better to validate the whole structure.

I also have no information whether the program should validate against duplicate UUID or IDs. I store the OutputLines in a List but if duplicates aren't allowed, I'd choose HashSet with the OutputLine's equals()/hashCode() using UUID or ID only. 

Not sure how to validate: likes, transport. 

Whenever I am not sure how to validate the field I assume something simple like: it should have a limit on number of characters, 
but in professional conditions I'd make an effort to discover the requirements, read the documentation, talk to other teams etc.

Not sure if any field should allow the trailing or leading spaces.

# Feature flag
Not sure who should be able to set up the flag: 
* should it be set up by some centralised mechanism (like the app properties or another endpoint)?
* should it be set up by a client? And if yes then how early should the flag be visible?
  * should we use headers? (The flag can be set up by the infrastructure and be pre-processed by filters)
  * should POST body be used? (it can get switched on/off from request to request, visible to Controller only)

Here I decided to use something simple, so I just send the flag from the body, but normally I'd aim to clarify this problem.





