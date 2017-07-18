function ECRegistryBridge() {
    var context = window.context;
    if (typeof context === "undefined" && typeof FakeECSmartRegistryContext !== "undefined") {
        context = new FakeECSmartRegistryContext();
    }

    return {
        getClients: function () {
            return JSON.parse(context.get());
        }
    };
}

function FakeECSmartRegistryContext() {
    return {
        get: function () {
            return JSON.stringify([
                {
                    "children": [
                        {
                            "dateOfBirth": "2013-07-10",
                            "entityId": "ff4fcdd8-3946-4185-8600-b4445608a87b",
                            "gender": "female"
                        }
                    ],
                    "dateOfBirth": "1984-07-12",
                    "ecNumber": 6,
                    "economicStatus": "bpl",
                    "entityId": "c18c3699-9e90-43f0-8963-d4117fa47a7a",
                    "entityIdToSavePhoto": "c18c3699-9e90-43f0-8963-d4117fa47a7a",
                    "familyPlanningMethodChangeDate": "2013-06-12",
                    "fpMethod": "ocp",
                    "husbandName": "Battiganahalli",
                    "village": "basavanapura",
                    "locationStatus": "in_area",
                    "name": "Mangala",
                    "numAbortions": "0",
                    "numLivingChildren": "0",
                    "numPregnancies": "0",
                    "numStillbirths": "0",
                    "numberOfOCPDelivered": "1",
                    "parity": "0",
                    "photo_path": "../../img/woman-placeholder.png",
                    "status": {
                        "date": "2013-06-12",
                        "type": "fp"
                    },
                    "isHighPriority": true
                },
                {
                    "caste": "sc",
                    "children": [
                        {
                            "dateOfBirth": "2013-07-10",
                            "entityId": "ff4fcdd8-3946-4185-8600-b4445608a87b",
                            "gender": "female"
                        },
                        {
                            "dateOfBirth": "2013-03-22",
                            "entityId": "8a1c44ef-9f8b-40bf-858f-a6ef4c48d943",
                            "gender": "female"
                        }
                    ],
                    "dateOfBirth": "1988-07-12",
                    "ecNumber": 23,
                    "economicStatus": "apl",
                    "entityId": "f4011e26-b29c-42d8-a46f-06216253de06",
                    "entityIdToSavePhoto": "f4011e26-b29c-42d8-a46f-06216253de06",
                    "familyPlanningMethodChangeDate": "2013-06-12",
                    "fpMethod": "ocp",
                    "husbandName": "yadayya",
                    "village": "kavalu_hosur",
                    "locationStatus": "in_area",
                    "name": "chetha",
                    "numAbortions": "0",
                    "numLivingChildren": "0",
                    "numPregnancies": "1",
                    "numStillbirths": "0",
                    "numberOfOCPDelivered": "1",
                    "parity": "0",
                    "photo_path": "../../img/woman-placeholder.png",
                    "status": {
                        "date": "2013-06-12",
                        "type": "fp"
                    },
                    "isHighPriority": false
                },
                {
                    "children": [
                        {
                            "dateOfBirth": "2013-07-10",
                            "entityId": "ff4fcdd8-3946-4185-8600-b4445608a87b",
                            "gender": "female"
                        },
                        {
                            "dateOfBirth": "2013-07-22",
                            "entityId": "8a1c44ef-9f8b-40bf-858f-a6ef4c48d943",
                            "gender": "female"
                        }
                    ],
                    "dateOfBirth": "1980-07-12",
                    "ecNumber": 2,
                    "entityId": "198041d9-3084-4ad8-ad64-fe7b2eca6a03",
                    "entityIdToSavePhoto": "198041d9-3084-4ad8-ad64-fe7b2eca6a03",
                    "familyPlanningMethodChangeDate": "2013-07-12",
                    "fpMethod": "male_sterilization",
                    "highPriorityReason": "Child_under_2 ",
                    "husbandName": "krishna",
                    "village": "hosa_agrahara",
                    "iudPlace": "chc",
                    "locationStatus": "in_area",
                    "name": "kanaka",
                    "numAbortions": "0",
                    "numLivingChildren": "2",
                    "numPregnancies": "1",
                    "numStillbirths": "0",
                    "parity": "1",
                    "photo_path": "../../img/woman-placeholder.png",
                    "status": {
                        "date": "2013-07-22",
                        "type": "pnc/fp",
                        "fpMethodDate": "2013-07-12"
                    },
                    "isHighPriority": false,
                    "caste": "st"

                },
                {
                    "caste": "c_others",
                    "children": [
                        {
                            "dateOfBirth": "2013-07-10",
                            "entityId": "ff4fcdd8-3946-4185-8600-b4445608a87b",
                            "gender": "female"
                        },
                        {
                            "dateOfBirth": "2013-07-22",
                            "entityId": "8a1c44ef-9f8b-40bf-858f-a6ef4c48d943",
                            "gender": "female"
                        }
                    ],
                    "dateOfBirth": "1987-09-17",
                    "ecNumber": 400,
                    "economicStatus": "apl",
                    "entityId": "e8e21744-2b9b-4e3e-830a-ba403608eb28",
                    "entityIdToSavePhoto": "e8e21744-2b9b-4e3e-830a-ba403608eb28",
                    "familyPlanningMethodChangeDate": "2012-05-20",
                    "fpMethod": "condom",
                    "husbandName": "putta",
                    "village": "munjanahalli",
                    "locationStatus": "in_area",
                    "name": "maanu",
                    "numAbortions": "0",
                    "numLivingChildren": "0",
                    "numPregnancies": "0",
                    "numStillbirths": "0",
                    "numberOfCondomsSupplied": "20",
                    "parity": "0",
                    "photo_path": "../../img/woman-placeholder.png",
                    "status": {
                        "date": "2013-02-27",
                        "type": "anc",
                        "edd": "2013-12-04"
                    },
                    "isHighPriority": true
                }
            ]);
        }
    };
}