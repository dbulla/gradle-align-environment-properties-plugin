# align-environment-properties-plugin
Plugin for Intellij IDEA which takes a series of property files (one per environment, plus an optional `application.properties`), and 
puts all the common and non-secret stuff in `application.properties` (if that doesn't exist, it's created), and then only environment-specific stuff
(dev is differnt than qa, for instance) and secret stuff (passwords, etc) are in the per-environmenet files.

The idea behind this is to help unclutter property files, which are often edited by hand, with lots of duplication and opportunities to
accidentally get one environment different from teh others when they're supposed to be the same.

So, anything that's common across all enviroinments goes into `application.properties`

In the plugin settings, you can add to the list of keywords that mark keys as secret.  The obvious ones are stuff like "password", "secret", etc.  You may have terms like "accesstoken", or prefer usernames also be considered secret.  If so, add them to this list.
