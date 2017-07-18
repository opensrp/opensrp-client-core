function ChildRegistryBridge() {
    var context = window.context;
    if (typeof context === "undefined" && typeof FakeChildSmartRegistryContext !== "undefined") {
        context = new FakeChildSmartRegistryContext();
    }

    return {
        getClients: function () {
            return JSON.parse(context.get());
        }
    };
}

function FakeChildSmartRegistryContext() {
    return {
        get: function () {
            return JSON.stringify([
                {
                    "alerts": [],
                    "caste": "sc",
                    "dob": "2013-07-23",
                    "ecNumber": "23",
                    "economicStatus": "apl",
                    "entityId": "c6a8d6f0-4602-43fb-a8e2-86c21f345624",
                    "entityIdToSavePhoto": "c6a8d6f0-4602-43fb-a8e2-86c21f345624",
                    "fatherName": "yadayya",
                    "gender": "",
                    "weight": "",
                    "locationStatus": "in_area",
                    "motherAge": "25",
                    "motherName": "chetha",
                    "photo_path": "../../img/icons/child-infant@3x.png",
                    "services_provided": [
                        {
                            "data": {
                                "vitaminADose": "1"
                            },
                            "date": "2013-07-23",
                            "name": "Vitamin A"
                        },
                        {
                            "name": "Illness Visit",
                            "date": "2013-07-24",
                            "data": {
                                "childSigns": "child signs",
                                "childSignsOther": "child signs other",
                                "sickVisitDate": "2013-07-24"
                            }
                        },
                        {
                            "name": "Illness Visit",
                            "date": "2013-07-18",
                            "data": {
                                "childSigns": "child signs",
                                "childSignsOther": "child signs other",
                                "sickVisitDate": "2013-07-18"
                            }
                        },
                        {
                            "name": "dptbooster_1",
                            "date": "2013-07-20"
                        }
                    ],
                    "thayiCardNumber": "",
                    "village": "kavalu_hosur",
                    "isHighRisk": false
                },
                {
                    "alerts": [],
                    "dob": "2013-07-22",
                    "ecNumber": "2",
                    "entityId": "8a1c44ef-9f8b-40bf-858f-a6ef4c48d943",
                    "entityIdToSavePhoto": "8a1c44ef-9f8b-40bf-858f-a6ef4c48d943",
                    "fatherName": "krishnaaaaaaaaaaaaaaaaaaaa",
                    "gender": "female",
                    "weight": "",
                    "locationStatus": "in_area",
                    "motherAge": null,
                    "motherName": "kanaka",
                    "photo_path": "../../img/icons/child-girlinfant@3x.png",
                    "services_provided": [
                        {
                            "data": {
                                "vitaminAPlace": "phc",
                                "vitaminADose": "2"
                            },
                            "date": "2013-07-10",
                            "name": "Vitamin A"
                        },
                        {
                            "data": {
                                "vitaminAPlace": "sub_center",
                                "vitaminADose": "3"
                            },
                            "date": "2013-07-16",
                            "name": "Vitamin A"
                        },
                        {
                            "data": {
                                "vitaminAPlace": "phc",
                                "vitaminADose": "5"
                            },
                            "date": "2013-07-17",
                            "name": "Vitamin A"
                        },
                        {
                            "name": "Illness Visit",
                            "date": "2013-07-23",
                            "data": {
                                "reportChildDisease": "report child disease",
                                "reportChildDiseaseOther": "report child disease other",
                                "reportChildDiseaseDate": "2013-07-13",
                                "reportChildDiseasePlace": "report child disease place",
                                "childReferral": "child referral"
                            }
                        }
                    ],
                    "thayiCardNumber": "",
                    "village": "hosa_agrahara",
                    "isHighRisk": false
                },
                {
                    "alerts": [],
                    "caste": "c_others",
                    "dob": "2013-07-10",
                    "ecNumber": "400",
                    "economicStatus": "apl",
                    "entityId": "46b780be-3897-4540-9944-2f4a2bce2f0c",
                    "entityIdToSavePhoto": "46b780be-3897-4540-9944-2f4a2bce2f0c",
                    "fatherName": "putta",
                    "gender": "female",
                    "locationStatus": "in_area",
                    "motherAge": "25",
                    "motherName": "maanu",
                    "photo_path": "../../img/icons/child-girlinfant@3x.png",
                    "services_provided": [

                    ],
                    "thayiCardNumber": "2234572",
                    "village": "munjanahalli",
                    "isHighRisk": false
                },
                {
                    "alerts": [],
                    "caste": "c_others",
                    "dob": "2013-07-03",
                    "ecNumber": "400",
                    "economicStatus": "apl",
                    "entityId": "68fca865-e5ca-4a95-9145-5575c915a456",
                    "entityIdToSavePhoto": "68fca865-e5ca-4a95-9145-5575c915a456",
                    "fatherName": "putta",
                    "gender": "female",
                    "locationStatus": "in_area",
                    "motherAge": "25",
                    "motherName": "maanu",
                    "photo_path": "../../img/icons/child-girlinfant@3x.png",
                    "services_provided": [
                    ],
                    "thayiCardNumber": "2234572",
                    "village": "munjanahalli",
                    "isHighRisk": false
                },
                {
                    "alerts": [],
                    "caste": "c_others",
                    "dob": "2013-07-08",
                    "ecNumber": "400",
                    "economicStatus": "apl",
                    "entityId": "897404fd-e73e-4161-915d-b360f659db0b",
                    "entityIdToSavePhoto": "897404fd-e73e-4161-915d-b360f659db0b",
                    "fatherName": "putta",
                    "gender": "female",
                    "locationStatus": "in_area",
                    "motherAge": "25",
                    "motherName": "maanu",
                    "photo_path": "../../img/icons/child-girlinfant@3x.png",
                    "services_provided": [
                        {
                            "data": {
                                "vitaminAPlace": "phc",
                                "vitaminADose": "3"
                            },
                            "date": "2013-07-16",
                            "name": "Vitamin A"
                        }
                    ],
                    "thayiCardNumber": "2234572",
                    "village": "munjanahalli",
                    "isHighRisk": false
                },
                {
                    "alerts": [
                        {
                            "date": "2013-07-22",
                            "name": "bcg",
                            "status": "normal"
                        }
                    ],
                    "caste": "sc",
                    "dob": "2013-07-08",
                    "economicStatus": "bpl",
                    "entityId": "6b33adea-c932-47a4-aaa5-52b117eaa64c",
                    "entityIdToSavePhoto": "6b33adea-c932-47a4-aaa5-52b117eaa64c",
                    "fatherName": "pavan",
                    "gender": "male",
                    "weight": "",
                    "locationStatus": "out_of_area",
                    "motherAge": "26",
                    "motherName": "pushpa",
                    "photo_path": "../../img/icons/child-infant@3x.png",
                    "services_provided": [],
                    "thayiCardNumber": "4563557",
                    "village": "madhavagere",
                    "isHighRisk": false
                },
                {
                    "alerts": [],
                    "caste": "sc",
                    "dob": "2013-06-23",
                    "economicStatus": "bpl",
                    "entityId": "0bfdee2e-2931-4e19-82d1-cae5997e72bd",
                    "entityIdToSavePhoto": "0bfdee2e-2931-4e19-82d1-cae5997e72bd",
                    "fatherName": "susheel",
                    "gender": "female",
                    "weight": "3.8",
                    "locationStatus": "out_of_area",
                    "motherAge": "24",
                    "motherName": "sapna ",
                    "photo_path": "../../img/icons/child-girlinfant@3x.png",
                    "services_provided": [
                        {
                            "date": "2013-06-23",
                            "name": "bcg"
                        },
                        {
                            "date": "2013-07-06",
                            "name": "opvbooster"
                        },
                        {
                            "date": "2013-06-23",
                            "name": "hepb_0"
                        },
                        {
                            "date": "2013-06-23",
                            "name": "measlesbooster"
                        },
                        {
                            "data": {
                                "vitaminADose": "1"
                            },
                            "date": "2013-07-23",
                            "name": "Vitamin A"
                        }
                    ],
                    "thayiCardNumber": "4675357",
                    "village": "megalapura",
                    "isHighRisk": false
                },
                {
                    "alerts": [],
                    "dob": "2013-07-23",
                    "entityId": "de9ecbc4-8f1f-44fd-9b29-b85e248b98bb",
                    "entityIdToSavePhoto": "de9ecbc4-8f1f-44fd-9b29-b85e248b98bb",
                    "fatherName": "shivu",
                    "gender": "male",
                    "weight": "4",
                    "locationStatus": "out_of_area",
                    "motherAge": "34",
                    "motherName": "smitha",
                    "photo_path": "../../img/icons/child-infant@3x.png",
                    "services_provided": [
                        {
                            "date": "2013-07-23",
                            "name": "opv_0"
                        },
                        {
                            "data": {
                                "vitaminADose": "1"
                            },
                            "date": "2013-07-23",
                            "name": "Vitamin A"
                        }
                    ],
                    "thayiCardNumber": "1233345",
                    "village": "vajamangala",
                    "isHighRisk": false
                },
                {
                    "alerts": [],
                    "dob": "2013-07-10",
                    "entityId": "ff4fcdd8-3946-4185-8600-b4445608a87b",
                    "entityIdToSavePhoto": "ff4fcdd8-3946-4185-8600-b4445608a87b",
                    "fatherName": "h2",
                    "gender": "female",
                    "weight": "5",
                    "locationStatus": "out_of_area",
                    "motherAge": "27",
                    "motherName": "w2",
                    "photo_path": "../../img/icons/child-girlinfant@3x.png",
                    "services_provided": [
                        {
                            "date": "2013-07-10",
                            "name": "bcg"
                        },
                        {
                            "date": "2013-07-15",
                            "name": "opv_1"
                        },
                        {
                            "date": "2013-07-15",
                            "name": "pentavalent_1"
                        },
                        {
                            "date": "2013-07-15",
                            "name": "opv_0"
                        }
                    ],
                    "thayiCardNumber": "9876543",
                    "village": "battiganahalli",
                    "isHighRisk": false
                }
            ]);
        }
    };
}