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
   * [Developer Guides](#developer-guides)
   * [Wiki](#wiki)
   * [Uses](#uses)
   * [Packages](#packages)

# Introduction

OpenSRP Client Core App/Module basically provides basic functionality such as networking, security, database access, common widgets, utilities, domain objects, service layer, broadcast receivers and syncing.

# Features

It provides:

1. Domain objects for forms and database.
2. Mappers between the different domains
3. Basic and advanced networking capabilities to securely and efficiently connect to an OpenSRP backend
4. Sync abilities that handle and maintain data consistency between the client and backend
5. Data creation, edit, retrieval and deletion capabilities on the client device
6. Security services that maintain global data and application security
7. Utilities used for file storage, caching, image rendering, logging, session management and number conversions.
8. Access to tailored android views/widgets for OpenSRP

# Why OpenSRP?

1. It provides client access on Android phones which are easily available and acquirable
2. It can work with minimal or no internet connection
3. It provides enhanced security
4. It primarily integrates with OpenMRS
5. It is tailored to be used by health workers who regularly provide outreach services
6. It generates custom reports eg. HIA 2
7. It manages stock levels for stock provided to the health workers
8. It implements the WHO-recommended **z-score** for child growth monitoring


# Website

If you are looking for more information regarding OpenSRP as a platform checkout the [OpenSRP Site](http://smartregister.org/)


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


# Uses

OpenSRP Client core has been used in several libraries and applications:

   * [OpenSRP Path application](https://github.com/OpenSRP/opensrp-client-path)
   * [OpenSRP KIP application](https://github.com/OpenSRP/opensrp-client-kip)
   * [OpenSRP Growth monitoring library](https://github.com/OpenSRP/opensrp-client-growth-monitoring)
   * [OpenSRP Immunization library](https://github.com/OpenSRP/opensrp-client-immunization)
   * [OpenSRP Native Form library](https://github.com/OpenSRP/opensrp-client-native-form)


# Main Functions

## 1. Security

Security is provided in the following:
   * Network - It supports SSL in cases where the backend server is connected through an `https` url
   * Data access - Only registered providers are able to view and manipulate records
   * Data encryption - The database on the android client is encrypted with 256-bit AES encryption using [SQLCipher](https://guardianproject.info/code/sqlcipher/).

The security classes can be found in `org.smartregister.ssl`

## 2. Data management

This app provides data management.
It implements both plain and secure data storage with those implementing secure storage extending `SQLiteOpenHelper`, `Repository` or `BaseRepository`.

The rest are plain and use the SQLite helpers provided by Android.

For this reason, there are multiple implementations for storing the same model.

Class | Represents
----- | --------------
`EventClientRepository` | Events
`AlertRepository` | Alerts
`ChildRepository` | Children
`ClientRepository` | Clients/Patients
`DetailsRepository` | Details
`EligibleCoupleRepository` | Eligible couples
`EventRepository` | Events
`FormDataRepository` | Form data
`FormsVersionRepository` | Form version
`ImageRepository` | Image locations
`MotherRepository` | Mothers
`ServiceProvidedRepository` | Provided service to the patient
`SettingsRepository` | App settings eg. connection configurations
`TimelineEventRepository` | Timeline events

The data management classes can be found in `org.smartregister.repository`

## 3. Networking

This app provides the following networking capabilities:

Class | Represents
----- | --------------
`OpensrpSSLHelper` | SSL Connection helper
`` | Asynchronous networking class
`HttpAgent` | Synchronous networking class with username\password ([Basic Auth](https://tools.ietf.org/html/rfc2617)) access support
`ConnectivityChangeReceiver` | Network status detection by a broadcast receiver
Thread safe connections
`GZipEncodingHttpClient` | GZip encoding and decoding capabilities
Session management
`UserService` | User authentication & Client-Server time synchronization

The networking classes can be found in:
   * `org.smartregister.service`
   * `org.smartregister.util`
   * `org.smartregister.client`
   * `org.smartregister.ssl`
   * `org.smartregister.view.receiver`


## 4. Domain Objects

This app provides the following domain objects:
<<<<<<< HEAD

Class | Represents
----- | ----------
`Address` | Location address containing map coordinates
`BaseDataObject` | Data object with datestamps, void flag, related void details and server version
`BaseEntity` | Extends `BaseDataObject` to include the `baseEntityId`, `identifiers`, `addresses` and `attributes` that are common in OpenSRP models
`Client` | Represents a patient in OpenSRP eg. a child. It contains relevant patient details and extends `BaseEntity`
`ColumnAttribute` | Represents a column using type, is-primary-key and is-index properties. It is used in the `EventClientRepository` class to define and access columns in the appropriate table
`Event` | It represents an event in OpenSRP which are mainly [encounters](#https://github.com/OpenSRP/opensrp-client-native-form#encounter-types) eg. Birth Registration, Death. It extends the `BaseDataObject` and provides other properties
`Obs` | It represents an observation in an `Event` _above_
`Query` | It represents a data query and enables creation of queries using an OOP approach
`FormData` | It represents form fields, their inputs and any sub-forms
`FormField` | It represents a single form question/field with a name, value and source
`FormInstance` | It represents a `FormData` of a specific definition version
`FormSubmission` | It represents the status of form before or after submission. It therefore contains other metadata such as client version and server verion.
`SubForm` | It represents a form inside another form
`Alert` | It represents a notification about an encounter which is due or overdue the expected time
`ANM` | It represents a healt services provider
`Child` | It represents a child
`EligibleCouple` | It represents an eligible couple
`FormDefinitionVersion` | It represents a form version
`Mother` | It represents a mother
`Photo` | It represents a photo by storing the file path & resource id
`ProfileImage` | It represents the photo of an entity
`Report` | It represents the report
`MontlyReport` | It represents a monthly report
`Response` | It represents a http response with status & payload
`ServiceProvided` | It represents a service that was provided to a patient
`TimelineEvent` | It represents an event within a patient's life eg. birth

The domain object classes can be found in `org.smartregister.domain`. There are several domains namely: global domain, form and database domain. 


## 5. Sync

This app provides the following sync capabilities:

   * Periodic syncing based on network connection

The sync classes can be found in `org.smartregister.sync`

## 6. Utitiles

This app provides the following utilties:
<<<<<<< HEAD

Class | Provides
----- | --------
`BitmapImageCache` | Improved image caching
`Cache` | Data caching and modifications listener
`FileUtilities` | File storage utility
`FloatUtil` | Float conversion utility
`FormSubmissionBuilder` | Form submission builders
`FormUtils` | Form generation and manipulation utility
`IntegerUtil` | Integer conversion utility
`JsonFormUtils` | JSON form data extractor and injector
`OpenSRPImageLoader` | Asynchronous image downloader with image caching
`Session` | Session manager
`StringUtil` | String manipulation utility
`TimelineEventComparator` | Timeline event comparator
`Utils` | Date conversion, android preference manipulator, view generator, metrics humanizer among others.


The utilities classes can be found under `org.smartregister.util`

## 7. Services

This app provides business logic for operations as follows:

Class | Business logic related to
----- | ----------
`ActionService` | Actions
`AlertService` | Alerts
`AllFormVersionSyncService` | Form versions
`ANMService` | Health service providers
`BeneficiaryService` | Beneficiaries
`ChildService` | Children
`Drishti` | Form submissions
`MotherService` | Mothers
`ServiceProvidedService` | Services provided

The service classes can be found in `org.smartregister.service`



