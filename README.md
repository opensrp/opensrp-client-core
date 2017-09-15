[![Build Status](https://travis-ci.org/OpenSRP/opensrp-client-core.svg?branch=master)](https://travis-ci.org/OpenSRP/opensrp-client-core) [![Coverage Status](https://coveralls.io/repos/github/OpenSRP/opensrp-client-core/badge.svg?branch=master)](https://coveralls.io/github/OpenSRP/opensrp-client-core?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/4a58cd4e1748432780ac66a9fbee0394)](https://www.codacy.com/app/OpenSRP/opensrp-client-core?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=OpenSRP/opensrp-client-core&amp;utm_campaign=Badge_Grade)

[![Dristhi](opensrp-app/res/drawable-mdpi/login_logo.png)](https://smartregister.atlassian.net/wiki/dashboard.action)


# Table of Contents

* [Introduction](#introduction)
* [Features](#features)
* [Why OpenSRP?](#why-opensrp)
* [Website](#website)
* [Developer Documentation](#developer-documentation)
   * [Pre-requisites](#pre-requisites)
   * [Installation Devices](#installation-devices)
   * [How to install](#how-to-install)
   * [Packages](#packages)

# Introduction

OpenSRP Client Core App/Module basically provides basic functionality such as networking, security, database access, common widget, utilities, domain objects, services, broadcast receivers and syncing.

# Features

1. It provides domain objects for forms and database.
2. It provides mappers between the different domains
3. It provides basic and advanced networking capabilities to securely and efficiently connect to an OpenSRP backend
4. It provides sync abilities that handle maintain data consistency between the client and backend
5. It provides data creation, edit, retrieval and deletion capabilities on the client device
6. It provide security services that maintains global data and application security
7. It provides utilities used for file storage, caching, image rendering, logging, session management and number conversions.
8. It provides access to tailored views for OpenSRP

# Why OpenSRP?

1. It provides client access on Android phones - Easily available, acquirable and 
2. It can work with minimal or no internet connection
3. Enhanced security
4. Fundamentally connects to OpenMRS
5. It is tailored to work for health workers who regularly provide outreach services
6. It generates custom reports
7. It manages stock levels provides to the health workers
8. It works on the most available
9. It implements the WHO-recommended **z-score** for child growth monitoring


# Website

If you are looking for more information regarding OpenSRP as a platform check [OpenSRP Site](http://smartregister.org/)


# Developer Documentation

This section will provide a brief description how to build and install the application from the repository source code.

## Pre-requisites

1. Make sure you have Java 1.7 to 1.8 installed
2. Make sure you have Android Studio installed or [download it from here](https://developer.android.com/studio/index.html)


## Installation Devices

1. Use a physical Android device to run the app
2. Use the Android Emulator that comes with the Android Studio installation (Slow & not advisable)
3. Use Genymotion Android Emulator
    * Go [here](https://www.genymotion.com/) and register for genymotion account if none. Free accounts have limitations which are not counter-productive
    * Download your OS Version of VirtualBox at [here](https://www.virtualbox.org/wiki/Downloads)
    * Install VirtualBox
    * Download Genymotion & Install it
    * Sign in to the genymotion app
    * Create a new Genymotion Virtual Device 
        * **Preferrable & Stable Choice** - API 22(Android 5.1.0), Screen size of around 800 X 1280, 1024 MB Memory --> eg. Google Nexus 7, Google Nexus 5

## How to install

1. Import the project into Android Studio by: **Import a gradle project** option
   _All the plugins required are explicitly stated, therefore it can work with any Android Studio version - Just enable it to download any packages not available offline_
1. Open Genymotion and Run the Virtual Device created previously.
1. Run the app on Android Studio and chose the Genymotion Emulator as the ` Deployment Target`


### Developer Guides

If you want to contribute please refer to these resources:

   * [Getting started with OpenSRP](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/6619148/Getting+started+with+OpenSRP)
   * [Setup Instructions](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/6619255/Setup+Instructions)
   * [Complete OpenSRP Developer's Guide](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/6619193/OpenSRP+Developer%27s+Guide)

### Wiki

If you are looking for detailed guides on how to install, configure, contribute and extend OpenSRP visit [OpenSRP Wiki](https://smartregister.atlassian.net/wiki)


# Main Functions

## 1. Security

Security is provided in the following:
   * Network - It supports ssl from client if the user uses a https url for the base server url setting
   * Data access - Only registered providers can be able to view and manipulate records
   * Data encryption - The database on Android client is encrypted

The security classes can be found in `org.smartregister.`

## 2. Data management

This app provides data management for the following:
   * Alerts
   * Eligible couples
   * Reports
   * Services Provided
   * Settings
   * Name-Value pairs
   * Timeline events
   * Children
   * Details
   * Events
   * Form data
   * Form Versions
   * Image Locations
   * Mothers
   * Reports
   * Device User
   * Connection configurations

The data management classes can be found in `org.smartregister.repository` & `org.smartregister.repository`

## 3. Networking

This app provides the following networking capabilities:
   * SSL Connection helper
   * Asynchronous networking classes
   * Synchronous
   * Network status detection
   * Thread safe connection
   * GZip encoding and decoding capabilities
   * Session management
   * Client-Server time and timezone synchronization
   * User authentication

The networking classes can be found in:
   * `org.smartregister.service`
   * `org.smartregister.util`
   * `org.smartregister.client`


## 4. Domain Objects

This app provides the following domain objects:
   * Database Domain
      * Address
      * Base Data Object
      * Base Entity
      * Client
      * Column
      * Column Attribute
      * Event
      * Filter Type
      * Obs
      * Query
   * Form Domain
      * Field Overrides
      * Form Data
      * Form Field
      * Form Instance
      * Form Submission
   * Global Domain
      * Alert
      * ANM(Health Services Provider)
      * Child
      * Eligible Couple
      * Form Definition Version
      * Mother
      * Photo
      * Profile Image
      * Report
      * Montly Report
      * Http response
      * Service Provided
      * Timeline Event

The domain object classes can be found in `org.smartregister.domain`

## 5. Sync

This app provides the following sync capabilities:
   * Periodic syncing based on network connection

## 6. Utitiles

This app provides the following utilties:
   * Improved Image caching
   * Data caching
   * File storage utilities
   * Float conversion utility
   * Form Submission builders
   * Form generation utitlity
   * Integer conversion utility
   * Json Form data extractor and injector
   * Image uploader
   * Session manager
   * String manipulation utility
   * Timeline events comparator

## 7. Services

This app provides business logic for operations related to:

   * Actions
   * Alerts
   * Form Versions
   * Health Service Provider
   * Beneficiaries
   * Patient/Child
   * Forms
   * Mothers
   * Services Provided

The sync classes can be found in `org.smartregister.sync`


