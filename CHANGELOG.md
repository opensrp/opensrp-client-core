 Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).


[Unreleased]
----------------------
##### Changed
- Refactored the entities around Server settings enities e.g. Charactersitic* Objects/Entities are now named ServerSetting* This is a breaking change
- Refactored the way server settings json file is stored in the settings repo, It now stores the whole server json to enable persistence of Metatdata - This is a breaking change
  Code that was retrieving settings from the setting repo should should refactor to get the settings by value.get(AllConstants.SETTINGS) after retrieving the value from the repo

##### Fixed

###Added

