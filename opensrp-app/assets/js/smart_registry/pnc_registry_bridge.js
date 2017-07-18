function PNCRegistryBridge() {
    var context = window.context;
    if (typeof context === "undefined" && typeof FakePNCSmartRegistryContext !== "undefined") {
        context = new FakePNCSmartRegistryContext();
    }

    return {
        getClients: function () {
            return JSON.parse(context.get());
        }
    };
}

function FakePNCSmartRegistryContext() {
    return {
        get: function () {
            return JSON.stringify([
                {
                    entityId: "entity id 1",
                    entityIdToSavePhoto: "entity id 1",
                    ec_number: '314',
                    village: 'Chikkabherya',
                    name: 'Zubina',
                    thayi: '4636587',
                    age: '24',
                    womanDOB: '1980-02-25',
                    husbandName: 'Billy Bob',
                    photo_path: "../../img/woman-placeholder.png",
                    isHighPriority: true,
                    isHighRisk: true,
                    locationStatus: "out_of_area",
                    economicStatus: "bpl",
                    caste: "sc",
                    iudPlace: "PNC",
                    iudPerson: "",
                    family_planning_method_change_date: '2013-08-13',
                    numberOfCondomsSupplied: null,
                    numberOfOCPDelivered: "8",
                    numberOfCentchromanPillsDelivered: null,
                    deliveryDate: "2013-06-15T00:00:00.000",
                    deliveryPlace:"PRIVATE_FACILITY",
                    deliveryComplications: "hemorrhage placenta_previa prolonged_or_obstructed_labour hemorrhage placenta_previa prolonged_or_obstructed_labour",
                    pncComplications: "abdominal_pain nipple_hardness difficulty_breathing",
                    deliveryType: "instrumental_forcep", //TODO: what are the options
                    otherDeliveryComplications: "",
                    alerts: [
                        {
                            name: 'PNC',
                            date: '2013-06-26',
                            status: 'urgent'
                        }
                    ],
                    services_provided: [
                        {
                            name: 'PNC',
                            date: '2013-06-16',
                            data: {
                                day: 14
                            }
                        },
                        {
                            name: 'PNC',
                            date: '2013-06-18',
                            data: {
                                day: 18
                            }
                        },
                        {
                            name: 'Not PNC',
                            date: '2013-06-09',
                            data: {}
                        }
                    ],
                    children: [
                        {
                            gender: 'female',
                            weight: '3.5'
                        }
                    ]
                },
                {
                    entityId: "entity id 2",
                    entityIdToSavePhoto: "entity id 2",
                    ec_number: '314',
                    village: 'Chikkabherya',
                    name: 'Radhia',
                    thayi: '3728256',
                    age: '21',
                    womanDOB: '1981-06-18',
                    husbandName: 'Bob Billy',
                    photo_path: "../../img/woman-placeholder.png",
                    isHighPriority: false,
                    isHighRisk: false,
                    locationStatus: "",
                    economicStatus: "apl",
                    caste: "st",
                    fp_method: "ocp",
                    iudPlace: "PNC",
                    iudPerson: "",
                    family_planning_method_change_date: '2013-08-13',
                    numberOfCondomsSupplied: null,
                    numberOfOCPDelivered: "10",
                    numberOfCentchromanPillsDelivered: null,
                    deliveryDate: "2013-05-13T00:00:00.000",
                    deliveryPlace: "PNC",
                    deliveryType: "Normal", //TODO: what are the options
                    deliveryComplications: "",
                    pncComplications: "",
                    otherDeliveryComplications: "",
                    alerts: [
                        {
                            name: 'PNC',
                            date: '2013-06-26',
                            status: 'complete'
                        }
                    ],
                    services_provided: [
                        {
                            name: 'PNC',
                            date: '2013-06-03',
                            data: {
                                day: 14
                            }
                        },
                        {
                            name: 'PNC',
                            date: '2013-06-07',
                            data: {
                                day: 18
                            }
                        },
                        {
                            name: 'PNC',
                            date: '2013-06-09',
                            data: {
                                day: 7
                            }
                        }
                    ]
                },
                {
                    entityId: "entity id 3",
                    entityIdToSavePhoto: "entity id 3",
                    ec_number: '314',
                    village: 'Chikkabherya',
                    name: 'Carolyn 314',
                    thayi: '4636587',
                    age: '24',
                    womanDOB: '1982-02-25',
                    husbandName: 'Billy Bob',
                    photo_path: "../../img/woman-placeholder.png",
                    isHighPriority: true,
                    isHighRisk: false,
                    locationStatus: "out_of_area",
                    economicStatus: "bpl",
                    caste: "st",
                    fp_method: "iud",
                    iudPlace: "PRIVATE_FACILITY",
                    iudPerson: "MO",
                    family_planning_method_change_date: '2013-08-13',
                    numberOfCondomsSupplied: "20",
                    numberOfOCPDelivered: null,
                    numberOfCentchromanPillsDelivered: null,
                    deliveryDate: "2013-06-10T00:00:00.000",
                    deliveryPlace: "PNC",
                    deliveryType: "Normal", //TODO: what are the options
                    deliveryComplications: "hemorrhage placenta_previa prolonged_or_obstructed_labour",
                    pncComplications: "",
                    otherDeliveryComplications: "",
                    alerts: [
                        {
                            name: 'PNC',
                            date: '2013-06-30',
                            status: 'normal'
                        }
                    ],
                    services_provided: [
                        {
                            name: 'PNC',
                            date: '2013-06-12',
                            data: {
                                day: 2
                            }
                        },
                        {
                            name: 'PNC',
                            date: '2013-06-18',
                            data: {
                                day: 8
                            }
                        }
                    ],
                    children: [
                        {
                            gender: 'female'
                        }
                    ]
                },
                {
                    entityId: "entity id 4",
                    entityIdToSavePhoto: "entity id 4",
                    ec_number: '314',
                    village: 'Chikkabherya',
                    name: 'Carolyn 4',
                    thayi: '4636587',
                    age: '24',
                    womanDOB: '1982-02-25',
                    husbandName: 'Billy Bob',
                    photo_path: "../../img/woman-placeholder.png",
                    isHighPriority: false,
                    isHighRisk: false,
                    locationStatus: "",
                    economicStatus: "BPL",
                    caste: "sc",
                    fp_method: "ocp",
                    iudPlace: "PNC",
                    iudPerson: "",
                    family_planning_method_change_date: '2013-08-13',
                    numberOfCondomsSupplied: null,
                    numberOfOCPDelivered: "8",
                    numberOfCentchromanPillsDelivered: null,
                    deliveryDate: "2013-06-18T00:00:00.000",
                    deliveryPlace: "PNC",
                    deliveryType: "Normal", //TODO: what are the options
                    deliveryComplications: "hemorrhage placenta_previa prolonged_or_obstructed_labour",
                    pncComplications: "difficulty_breathing",
                    otherDeliveryComplications: "",
                    alerts: [
                        {
                            name: 'PNC',
                            date: '2013-06-30',
                            status: 'normal'
                        }
                    ],
                    services_provided: [
                        {
                            name: 'PNC',
                            date: '2013-06-19',
                            data: {
                                day: 2
                            }
                        }
                    ],
                    children: [
                        {
                            gender: 'female',
                            weight: '3.5'
                        }
                    ]
                },
                {
                    entityId: "entity id 5",
                    entityIdToSavePhoto: "entity id 5",
                    ec_number: '314',
                    village: 'Chikkabherya',
                    name: 'Carolyn',
                    thayi: '4636587',
                    age: '24',
                    womanDOB: '1982-02-25',
                    husbandName: 'Billy Bob',
                    photo_path: "../../img/woman-placeholder.png",
                    isHighPriority: true,
                    isHighRisk: false,
                    locationStatus: "out_of_area",
                    economicStatus: "bpl",
                    caste: "st",
                    fp_method: "condom",
                    iudPlace: "PNC",
                    iudPerson: "",
                    family_planning_method_change_date: '2013-08-13',
                    numberOfCondomsSupplied: null,
                    numberOfOCPDelivered: "8",
                    numberOfCentchromanPillsDelivered: null,
                    deliveryDate: "2013-06-10T00:00:00.000",
                    deliveryPlace: "PNC",
                    deliveryType: "Normal", //TODO: what are the options
                    deliveryComplications: "hemorrhage placenta_previa prolonged_or_obstructed_labour",
                    pncComplications: "abdominal_pain nipple_hardness",
                    otherDeliveryComplications: "",
                    alerts: [
                        {
                            name: 'PNC',
                            date: '2013-06-30',
                            status: 'normal'
                        }
                    ],
                    services_provided: [
                        {
                            name: 'PNC',
                            date: '2013-06-12',
                            data: {
                                day: 2
                            }
                        },
                        {
                            name: 'PNC',
                            date: '2013-06-18',
                            data: {
                                day: 8
                            }
                        }
                    ],
                    children: [
                        {
                            gender: 'female',
                            weight: '3.5'
                        }
                    ]
                },
                {
                    entityId: "entity id 6",
                    entityIdToSavePhoto: "entity id 6",
                    ec_number: '314',
                    village: 'Chikkabherya',
                    name: 'Radhia',
                    thayi: '3728256',
                    age: '21',
                    womanDOB: '1981-06-18',
                    husbandName: 'Bob Billy',
                    photo_path: "../../img/woman-placeholder.png",
                    isHighPriority: false,
                    isHighRisk: true,
                    locationStatus: "",
                    economicStatus: "bpl",
                    caste: "st",
                    fp_method: "ocp",
                    iudPlace: "PNC",
                    iudPerson: "",
                    family_planning_method_change_date: '2013-08-13',
                    numberOfCondomsSupplied: null,
                    numberOfOCPDelivered: "10",
                    numberOfCentchromanPillsDelivered: null,
                    deliveryDate: "2012-12-16T00:00:00.000",
                    deliveryPlace: "PNC",
                    deliveryType: "Normal", //TODO: what are the options
                    pncComplications: "abdominal_pain",
                    deliveryComplications: "",
                    otherDeliveryComplications: "",
                    alerts: [

                    ],
                    services_provided: [
                        {
                            name: 'PNC',
                            date: '2013-06-17',
                            data: {
                                day: 14
                            }
                        },
                        {
                            name: 'PNC',
                            date: '2013-06-19',
                            data: {
                                day: 18
                            }
                        }
                    ]
                },
                {
                    entityId: "entity id 7",
                    entityIdToSavePhoto: "entity id 7",
                    ec_number: '314',
                    village: 'Chikkabherya',
                    name: 'Radhia',
                    thayi: '3728256',
                    age: '21',
                    womanDOB: '1981-06-18',
                    husbandName: 'Bob Billy',
                    photo_path: "../../img/woman-placeholder.png",
                    isHighPriority: false,
                    isHighRisk: true,
                    locationStatus: "",
                    economicStatus: "apl",
                    caste: "st",
                    fp_method: "ocp",
                    iudPlace: "PNC",
                    iudPerson: "",
                    family_planning_method_change_date: '2013-08-13',
                    numberOfCondomsSupplied: null,
                    numberOfOCPDelivered: "10",
                    numberOfCentchromanPillsDelivered: null,
                    deliveryDate: "2013-06-22T00:00:00.000",
                    deliveryPlace:"PNC",
                    deliveryType: "Normal", //TODO: what are the options
                    pncComplications: "nipple_hardness",
                    deliveryComplications: "",
                    otherDeliveryComplications: "",
                    alerts: [

                    ],
                    services_provided: [
                        {
                            "data": {
                                "pncVisitNumber": "1",
                                day: 1
                            },
                            "date": "2013-06-23",
                            "name": "PNC"
                        },
                        {
                            "data": {
                                "pncVisitNumber": "2",
                                day: 3
                            },
                            "date": "2013-06-25",
                            "name": "PNC"
                        },
                        {
                            "data": {
                                "pncVisitNumber": "3",
                                day: 5
                            },
                            "date": "2013-06-27",
                            "name": "PNC"
                        },
                        {
                            "data": {
                                "pncVisitNumber": "4",
                                day: 7
                            },
                            "date": "2013-06-29",
                            "name": "PNC"
                        },
                        {
                            "data": {
                                "pncVisitNumber": "5",
                                day: 9
                            },
                            "date": "2013-07-01",
                            "name": "PNC"
                        },
                        {
                            "data": {
                                "pncVisitNumber": "6",
                                day: 12
                            },
                            "date": "2013-07-04",
                            "name": "PNC"
                        },
                        {
                            "data": {
                                "pncVisitNumber": "7",
                                day: 10
                            },
                            "date": "2013-07-02",
                            "name": "PNC"
                        },
                        {
                            "data": {
                                "pncVisitNumber": "8",
                                day: 13
                            },
                            "date": "2013-07-05",
                            "name": "PNC"
                        }
                    ]
                }
            ]);
        }
    };
}