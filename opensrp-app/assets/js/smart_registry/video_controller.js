angular.module("smartRegistry.controllers")
    .controller("VideoController", ["$scope", function ($scope) {
        FAMILY_PLANNING = 1;
        ANTENATAL = 2;
        POST_PARTUM = 3;
        CHILD_WELLNESS = 4;

        $scope.navigationBridge = new ANMNavigationBridge();
        $scope.videosBridge = new VideoBridge();

        $scope.goBack = function () {
            $scope.navigationBridge.goBack();
        };

        $scope.videoCategories = [
            {
                id: FAMILY_PLANNING,
                title: "Family Planning",
                num_videos: 6,
                css_class: "icon-video-fp"
            },
            {
                id: ANTENATAL,
                title: "Antenatal",
                num_videos: 4,
                css_class: "icon-video-antenatal"
            },
            {
                id: POST_PARTUM,
                title: "Post Partum",
                num_videos: 1,
                css_class: "icon-video-pp"
            },
            {
                id: CHILD_WELLNESS,
                title: "Child Wellness",
                num_videos: 4,
                css_class: "icon-video-wellness"
            }
        ];

        var defaultCategoryId = FAMILY_PLANNING;
        $scope.currentCategoryId = defaultCategoryId;

        $scope.videoList = [
            {
                id: "Introduction to Family Planning",
                title: "Introduction to FP",
                time: "4.42",
                previewImageUrl: "../../img/smart_registry/videos-icons/fp-FP-Introduction.jpg",
                category: FAMILY_PLANNING
            },
            {
                id: "Condom",
                title: "Condom",
                time: "4.42",
                previewImageUrl: "../../img/smart_registry/videos-icons/fp-Condom.jpg",
                category: FAMILY_PLANNING
            },
            {
                id: "OCP",
                title: "OCP",
                time: "4.42",
                previewImageUrl: "../../img/smart_registry/videos-icons/fp-OCP.jpg",
                category: FAMILY_PLANNING
            },
            {
                id: "IUD",
                title: "IUCD",
                time: "4.42",
                previewImageUrl: "../../img/smart_registry/videos-icons/fp-IUD.jpg",
                category: FAMILY_PLANNING
            },
            {
                id: "Female Sterilization",
                title: "Female Sterilization",
                time: "4.42",
                previewImageUrl: "../../img/smart_registry/videos-icons/fp-Tubal-ligation.jpg",
                category: FAMILY_PLANNING
            },
            {
                id: "Male Sterilization",
                title: "Male Sterilization",
                time: "4.42",
                previewImageUrl: "../../img/smart_registry/videos-icons/fp-Vasectomy.jpg",
                category: FAMILY_PLANNING
            },
            {
                id: "ANC General",
                title: "ANC General",
                time: "4.42",
                previewImageUrl: "../../img/smart_registry/videos-icons/anc-What-to-expect-when-expecting.jpg",
                category: ANTENATAL
            },
            {
                id: "IFA",
                title: "IFA",
                time: "4.42",
                previewImageUrl: "../../img/smart_registry/videos-icons/anc-IFA-supplement.jpg",
                category: ANTENATAL
            },
            {
                id: "Birth Plan",
                title: "Birth Plan",
                time: "4.42",
                previewImageUrl: "../../img/smart_registry/videos-icons/anc-Birth-plan.jpg",
                category: ANTENATAL
            },
            {
                id: "Danger Signs",
                title: "Danger Signs",
                time: "4.42",
                previewImageUrl: "../../img/smart_registry/videos-icons/anc-Danger-signs-pregnancy.jpg",
                category: ANTENATAL
            },
            {
                id: "Introduction to PNC",
                title: "Introduction to PNC",
                time: "4.42",
                previewImageUrl: "../../img/smart_registry/videos-icons/pnc-Post-partum.jpg",
                category: POST_PARTUM
            },
            {
                id: "Child Overview",
                title: "Child Overview",
                time: "4.42",
                previewImageUrl: "../../img/smart_registry/videos-icons/child-Overview-child-wellness.jpg",
                category: CHILD_WELLNESS
            },
            {
                id: "Diarrhea",
                title: "Diarrhea",
                time: "4.42",
                previewImageUrl: "../../img/smart_registry/videos-icons/child-Diarrhoeal-disease.jpg",
                category: CHILD_WELLNESS
            },
            {
                id: "ARI",
                title: "ARI",
                time: "4.42",
                previewImageUrl: "../../img/smart_registry/videos-icons/child-Acute-respiratory-infection.jpg",
                category: CHILD_WELLNESS
            },
            {
                id: "Malnutrition",
                title: "Malnutrition",
                time: "4.42",
                previewImageUrl: "../../img/smart_registry/videos-icons/child-Malnutrition.jpg",
                category: CHILD_WELLNESS
            }
        ];

        $scope.setCurrentCategory = function (id) {
            $scope.currentCategoryId = id;
        };

        $scope.videoClicked = function (videoName) {
            $scope.videosBridge.play(videoName);
        };
    }]);