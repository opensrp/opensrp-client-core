(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['anc_detail'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, stack2, options, functionType="function", escapeExpression=this.escapeExpression, helperMissing=helpers.helperMissing, self=this;

function program1(depth0,data) {
  
  var buffer = "", stack1, stack2;
  buffer += "\n<div id=\"warning\"></div>\n<div id=\"warning-modal-container\" class=\"modal-container\" style=\"display: block;\">\n    <div id=\"modal-goes-here\">\n        <div id=\"ancWarningModal\" class=\"edd-modal-content\">\n            <div class=\"edd-popup\"><img class=\"edd-popup-image\"></div>\n            <div class=\"big-text-black edd-modal-text\">EDD "
    + escapeExpression(((stack1 = ((stack1 = depth0.pregnancyDetails),stack1 == null || stack1 === false ? stack1 : stack1.daysPastEdd)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + " days past due!</div>\n            <div class=\"edd-modal-button\">\n                <button id=\"deliveryOutcomeFormButton\" class=\"btn btn-large btn-primary\" type=\"button\"\n                        data-form=\"delivery_outcome\" data-caseid=\"";
  if (stack2 = helpers.caseId) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.caseId; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "\">\n                    <div class=\"edd-modal-button-text\">Delivery Outcome Form</div>\n                </button>\n            </div>\n            <div class=\"edd-modal-button\">\n                <button id=\"goToProfileButton\" class=\"btn edd-modal-row\" type=\"button\">1\n                    <div class=\"edd-modal-button-text\">Go To Profile</div>\n                </button>\n            </div>\n        </div>\n    </div>\n</div>\n";
  return buffer;
  }

function program3(depth0,data) {
  
  
  return "\n        <span class=\"left-text\">ANC</span>\n        ";
  }

function program5(depth0,data) {
  
  
  return "\n        <span class=\"left-text\">Out of Area ANC</span>\n        ";
  }

function program7(depth0,data) {
  
  
  return "\n        <span class=\"right-text high-risk\">High Risk</span>\n        ";
  }

function program9(depth0,data) {
  
  
  return "\n        <span class=\"right-text normal-risk\">Normal Risk</span>\n        ";
  }

function program11(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "EC No: "
    + escapeExpression(((stack1 = ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.ecNumber)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + ", ";
  return buffer;
  }

function program13(depth0,data) {
  
  var buffer = "", stack1, options;
  buffer += "\n                        <div class=\"big-text high-risk\">\n                            <i class=\"icon-circle-arrow-up priority-indicator-icon\"></i> High Risk</span>\n                        </div>\n                        <div class=\"beneficiary-detail-footer risk-detail-text\">\n                            <ul>";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.camelCaseAndConvertToListItems || depth0.camelCaseAndConvertToListItems),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.highRiskReason), options) : helperMissing.call(depth0, "camelCaseAndConvertToListItems", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.highRiskReason), options)))
    + "</ul>\n                        </div>\n                        ";
  return buffer;
  }

function program15(depth0,data) {
  
  
  return "\n                        <div class=\"text-center big-text normal-risk\">Normal Risk</div>\n                        ";
  }

function program17(depth0,data) {
  
  
  return "\n                <div class=\"row-fluid unavailable-data\">\n                    No medical history data available\n                </div>\n                ";
  }

function program19(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\n                <div class=\"row-fluid beneficiary-detail-component\">\n                    <div class=\"beneficiary-detail-content\">\n                        <div class=\"timeline\">\n                            ";
  stack1 = helpers.each.call(depth0, depth0.timelineEvents, {hash:{},inverse:self.noop,fn:self.program(20, program20, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n                        </div>\n                        <div class=\"container-section-component row-fluid show-more-button\">\n                            <div class=\"divider\"></div>\n                            <div class=\"expand\"> Show more</div>\n                        </div>\n                    </div>\n                </div>\n                ";
  return buffer;
  }
function program20(depth0,data) {
  
  var buffer = "", stack1, stack2;
  buffer += "\n                            <div class=\"timeline-component row-fluid timelineEvent\">\n                                <div class=\"span3 type "
    + escapeExpression(((stack1 = depth0.type),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "\"></div>\n                                <div class=\"span5\">\n                                    <strong>";
  if (stack2 = helpers.title) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.title; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "</strong><br>\n                                    ";
  stack2 = helpers.each.call(depth0, depth0.details, {hash:{},inverse:self.noop,fn:self.program(21, program21, data),data:data});
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n                                </div>\n                                <div class=\"span4 pull-right text-right\">\n                                    ";
  if (stack2 = helpers.date) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.date; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "\n                                </div>\n                            </div>\n                            ";
  return buffer;
  }
function program21(depth0,data) {
  
  var buffer = "", stack1, stack2, options;
  buffer += "\n                                    ";
  options = {hash:{},data:data};
  stack2 = ((stack1 = helpers.formatText || depth0.formatText),stack1 ? stack1.call(depth0, depth0, options) : helperMissing.call(depth0, "formatText", depth0, options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "<br>\n                                    ";
  return buffer;
  }

  stack2 = helpers['if'].call(depth0, ((stack1 = depth0.pregnancyDetails),stack1 == null || stack1 === false ? stack1 : stack1.isEDDPassed), {hash:{},inverse:self.noop,fn:self.program(1, program1, data),data:data});
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n<div class=\"navbar navbar-fixed-top\">\n    <div class=\"navbar-inner\">\n        <ul class=\"area-6_5 nav pull-left\">\n            <li class=\"area-7_5\">\n                <div class=\"navbar-header ellipsis\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatText || depth0.formatText),stack1 ? stack1.call(depth0, ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.wifeName), options) : helperMissing.call(depth0, "formatText", ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.wifeName), options)))
    + "</div>\n            </li>\n        </ul>\n\n        <ul class=\"nav pull-right\">\n            <li><img class=\"separator\"></li>\n            <li data-modal-target=\"#ancFormModal\"><img class=\"update-data\"></li>\n        </ul>\n    </div>\n</div>\n\n<div id=\"ancFormModal\" class=\"modal-content\">\n    <div class=\"modal-row\" data-form=\"anc_visit\" data-caseid=\"";
  if (stack2 = helpers.caseId) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.caseId; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "\">ANC visit</div>\n    <div class=\"divider\"></div>\n    <div class=\"modal-row\" data-form=\"delivery_outcome\" data-caseid=\"";
  if (stack2 = helpers.caseId) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.caseId; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "\">Delivery outcome</div>\n    <div class=\"divider\"></div>\n    <div class=\"modal-row\" data-form=\"anc_close\" data-caseid=\"";
  if (stack2 = helpers.caseId) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.caseId; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "\">Close ANC</div>\n</div>\n\n<div id=\"content\" class=\"content-detail\">\n    <div class=\"status-bar\">\n        ";
  stack2 = helpers['if'].call(depth0, ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.isInArea), {hash:{},inverse:self.program(5, program5, data),fn:self.program(3, program3, data),data:data});
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n        ";
  options = {hash:{},inverse:self.program(9, program9, data),fn:self.program(7, program7, data),data:data};
  stack2 = ((stack1 = helpers.ifFalse || depth0.ifFalse),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.isHighRisk), options) : helperMissing.call(depth0, "ifFalse", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.isHighRisk), options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n    </div>\n    <div class=\"registry-container container-fluid\">\n        <div class=\"row-fluid\">\n            <div class=\"span4\">\n                <img id=\"womanPhoto\" src=\"file://"
    + escapeExpression(((stack1 = ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.photo_path)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "\">\n            </div>\n            <div class=\"span8\">\n                <ul class=\"client-details unstyled\">\n                    <li>\n                        <div class=\"row-fluid\">\n                            <div class=\"span9 big-text\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatText || depth0.formatText),stack1 ? stack1.call(depth0, ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.wifeName), options) : helperMissing.call(depth0, "formatText", ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.wifeName), options)))
    + "</div>\n                            <div class=\"span3 social-vulnerability text-right\">\n                                ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatSocialVulnerability || depth0.formatSocialVulnerability),stack1 ? stack1.call(depth0, ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.caste), ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.economicStatus), options) : helperMissing.call(depth0, "formatSocialVulnerability", ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.caste), ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.economicStatus), options)))
    + "\n                            </div>\n                        </div>\n                    </li>\n                    <li class=\"medium-text spacing-below\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.capitalize || depth0.capitalize),stack1 ? stack1.call(depth0, ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.husbandName), options) : helperMissing.call(depth0, "capitalize", ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.husbandName), options)))
    + "</li>\n                    <li>";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatText || depth0.formatText),stack1 ? stack1.call(depth0, ((stack1 = depth0.location),stack1 == null || stack1 === false ? stack1 : stack1.villageName), options) : helperMissing.call(depth0, "formatText", ((stack1 = depth0.location),stack1 == null || stack1 === false ? stack1 : stack1.villageName), options)))
    + "</li>\n                    <li class=\"light-text\">";
  stack2 = helpers['if'].call(depth0, ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.isInArea), {hash:{},inverse:self.noop,fn:self.program(11, program11, data),data:data});
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "Thayi\n                        No: ";
  if (stack2 = helpers.thayiCardNumber) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.thayiCardNumber; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "\n                    </li>\n                </ul>\n            </div>\n        </div>\n    </div>\n\n    <div class=\"divider\"></div>\n\n\n    <div class=\"registry-container container-fluid\">\n        <div class=\"big-text row-fluid detail-section-header\">\n            Summary\n        </div>\n        <div class=\"well well-for-beneficiary-details-no-padding\">\n            <div class=\"container-fluid container-no-padding\">\n                <div class=\"row-fluid beneficiary-detail-component\">\n                    <div class=\"beneficiary-detail-content\">\n                        ";
  options = {hash:{},inverse:self.program(15, program15, data),fn:self.program(13, program13, data),data:data};
  stack2 = ((stack1 = helpers.ifFalse || depth0.ifFalse),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.isHighRisk), options) : helperMissing.call(depth0, "ifFalse", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.isHighRisk), options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n                    </div>\n                    <div class=\"divider\"></div>\n                    <div class=\"beneficiary-detail-content\">\n                        <div class=\"span6 text-center right-border info\">\n                            <span class=\"big-text-numbers\">"
    + escapeExpression(((stack1 = ((stack1 = depth0.pregnancyDetails),stack1 == null || stack1 === false ? stack1 : stack1.monthsPregnant)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</span><br><span\n                                class=\"meta-summary-text\">Months Pregnant</span>\n                        </div>\n                        <div class=\"span6 text-center info\">\n                            <span class=\"big-text-numbers\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatDate || depth0.formatDate),stack1 ? stack1.call(depth0, ((stack1 = depth0.pregnancyDetails),stack1 == null || stack1 === false ? stack1 : stack1.edd), options) : helperMissing.call(depth0, "formatDate", ((stack1 = depth0.pregnancyDetails),stack1 == null || stack1 === false ? stack1 : stack1.edd), options)))
    + "</span><br><span\n                                class=\"meta-summary-text\">EDD</span>\n                        </div>\n                    </div>\n                </div>\n            </div>\n        </div>\n    </div>\n\n    <div class=\"registry-container container-fluid\">\n        <div class=\"big-text row-fluid detail-section-header\">\n            Timeline\n        </div>\n        <div class=\"well well-for-beneficiary-details-no-padding\">\n            <div class=\"container-fluid container-no-padding\">\n                ";
  options = {hash:{},inverse:self.program(19, program19, data),fn:self.program(17, program17, data),data:data};
  stack2 = ((stack1 = helpers.ifequal || depth0.ifequal),stack1 ? stack1.call(depth0, ((stack1 = depth0.timelineEvents),stack1 == null || stack1 === false ? stack1 : stack1.length), 0, options) : helperMissing.call(depth0, "ifequal", ((stack1 = depth0.timelineEvents),stack1 == null || stack1 === false ? stack1 : stack1.length), 0, options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n            </div>\n        </div>\n    </div>\n</div>";
  return buffer;
  });
templates['anm_navigation'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, stack2, options, functionType="function", escapeExpression=this.escapeExpression, helperMissing=helpers.helperMissing;


  buffer += "<div class=\"span2\" id=\"ec-register\">\n    <a class=\"btn btn-register\" id=\"ecSmartRegistryOption\">\n        <span id=\"eligibleCoupleCount\" class=\"register-count\">";
  if (stack1 = helpers.eligibleCoupleCount) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.eligibleCoupleCount; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</span>\n        <span class=\"register-title\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.internationalize || depth0.internationalize),stack1 ? stack1.call(depth0, "home_ec_label", options) : helperMissing.call(depth0, "internationalize", "home_ec_label", options)))
    + "</span>\n        <span>";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.internationalize || depth0.internationalize),stack1 ? stack1.call(depth0, "register_label", options) : helperMissing.call(depth0, "internationalize", "register_label", options)))
    + "</span>\n    </a>\n</div>\n<div class=\"span2\" id=\"fp-register\">\n    <a class=\"btn btn-register\" id=\"fpSmartRegistryOption\">\n        <span id=\"fpCount\" class=\"register-count\">";
  if (stack2 = helpers.fpCount) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.fpCount; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "</span>\n        <span class=\"register-title\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.internationalize || depth0.internationalize),stack1 ? stack1.call(depth0, "home_fp_label", options) : helperMissing.call(depth0, "internationalize", "home_fp_label", options)))
    + "</span>\n        <span>";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.internationalize || depth0.internationalize),stack1 ? stack1.call(depth0, "register_label", options) : helperMissing.call(depth0, "internationalize", "register_label", options)))
    + "</span>\n    </a>\n</div>\n<div class=\"span2\" id=\"anc-register\">\n    <a class=\"btn btn-register\" id=\"ancSmartRegistryOption\">\n        <span id=\"ancCount\" class=\"register-count\">";
  if (stack2 = helpers.ancCount) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.ancCount; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "</span>\n        <span class=\"register-title\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.internationalize || depth0.internationalize),stack1 ? stack1.call(depth0, "home_anc_label", options) : helperMissing.call(depth0, "internationalize", "home_anc_label", options)))
    + "</span>\n        <span>";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.internationalize || depth0.internationalize),stack1 ? stack1.call(depth0, "register_label", options) : helperMissing.call(depth0, "internationalize", "register_label", options)))
    + "</span>\n    </a>\n</div>\n<div class=\"span2\" id=\"pnc-register\">\n    <a class=\"btn btn-register\" id=\"pncSmartRegistryOption\">\n        <span id=\"pncCount\" class=\"register-count\">";
  if (stack2 = helpers.pncCount) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.pncCount; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "</span>\n        <span class=\"register-title\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.internationalize || depth0.internationalize),stack1 ? stack1.call(depth0, "home_pnc_label", options) : helperMissing.call(depth0, "internationalize", "home_pnc_label", options)))
    + "</span>\n        <span>";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.internationalize || depth0.internationalize),stack1 ? stack1.call(depth0, "register_label", options) : helperMissing.call(depth0, "internationalize", "register_label", options)))
    + "</span>\n    </a>\n</div>\n<div class=\"span2\" id=\"child-register\">\n    <a class=\"btn btn-register\" id=\"childMenuOption\">\n        <span id=\"childCount\" class=\"register-count\">";
  if (stack2 = helpers.childCount) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.childCount; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "</span>\n        <span class=\"register-title\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.internationalize || depth0.internationalize),stack1 ? stack1.call(depth0, "home_child_label", options) : helperMissing.call(depth0, "internationalize", "home_child_label", options)))
    + "</span>\n        <span>";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.internationalize || depth0.internationalize),stack1 ? stack1.call(depth0, "register_label", options) : helperMissing.call(depth0, "internationalize", "register_label", options)))
    + "</span>\n    </a>\n</div>\n";
  return buffer;
  });
templates['birth_plan'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var stack1, stack2, options, helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression, functionType="function", self=this;

function program1(depth0,data) {
  
  var buffer = "", stack1, stack2, options;
  buffer += "\n<div class=\"registry-container container-fluid\">\n    <div class=\"big-text row-fluid detail-section-header\">\n        Delivery Plan\n        <div class=\"edit-birth-plan btn btn-primary pull-right\" data-form=\"birth_plan\" data-caseid=\"";
  if (stack1 = helpers.caseId) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.caseId; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "\">Edit</div>\n    </div>\n    <div class=\"well well-for-beneficiary-details-no-padding\">\n        <div class=\"container-fluid container-no-padding\">\n            <div class=\"delivery-plan\">\n\n                <div class=\"row-fluid beneficiary-detail-component\">\n                    <div class=\"span2\">\n                        ";
  options = {hash:{},data:data};
  stack2 = ((stack1 = helpers.imageForDeliveryFacility || depth0.imageForDeliveryFacility),stack1 ? stack1.call(depth0, depth0.details, options) : helperMissing.call(depth0, "imageForDeliveryFacility", depth0.details, options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n                    </div>\n                    <div class=\"span6 big-text-black\">\n                        Facility\n                    </div>\n                    <div class=\"span4 meta-summary-text-with-margin\">\n                        ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatDeliveryFacilityType || depth0.formatDeliveryFacilityType),stack1 ? stack1.call(depth0, depth0.details, options) : helperMissing.call(depth0, "formatDeliveryFacilityType", depth0.details, options)))
    + "\n                    </div>\n                </div>\n\n                <div class=\"divider\"></div>\n\n                <div class=\"row-fluid beneficiary-detail-component\">\n                    <div class=\"span2\">\n                        ";
  options = {hash:{},data:data};
  stack2 = ((stack1 = helpers.imageBasedOnValueIsEmptyOrNot || depth0.imageBasedOnValueIsEmptyOrNot),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.transportPlan), options) : helperMissing.call(depth0, "imageBasedOnValueIsEmptyOrNot", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.transportPlan), options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n                    </div>\n                    <div class=\"span6 big-text-black\">\n                        Transportation\n                    </div>\n                    <div class=\"span4 meta-summary-text-with-margin\">\n                        ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatTransportPlan || depth0.formatTransportPlan),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.transportPlan), options) : helperMissing.call(depth0, "formatTransportPlan", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.transportPlan), options)))
    + "\n                    </div>\n                </div>\n\n                <div class=\"divider\"></div>\n\n                ";
  options = {hash:{},inverse:self.noop,fn:self.program(2, program2, data),data:data};
  stack2 = ((stack1 = helpers.ifFalse || depth0.ifFalse),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.isHighRisk), options) : helperMissing.call(depth0, "ifFalse", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.isHighRisk), options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n\n                <div class=\"row-fluid beneficiary-detail-component\">\n                    <div class=\"span2\">\n                        ";
  options = {hash:{},data:data};
  stack2 = ((stack1 = helpers.imageBasedOnYesOrNo || depth0.imageBasedOnYesOrNo),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.isThereABirthCompanion), options) : helperMissing.call(depth0, "imageBasedOnYesOrNo", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.isThereABirthCompanion), options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n                    </div>\n                    <div class=\"span6 big-text-black\">\n                        Birth companion\n                    </div>\n                    <div class=\"span4 meta-summary-text-with-margin\">\n                        ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatBooleanToYesOrNo || depth0.formatBooleanToYesOrNo),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.isThereABirthCompanion), options) : helperMissing.call(depth0, "formatBooleanToYesOrNo", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.isThereABirthCompanion), options)))
    + "\n                    </div>\n                </div>\n\n                <div class=\"divider\"></div>\n\n                <div class=\"row-fluid beneficiary-detail-component\">\n                    <div class=\"span2\">\n                        ";
  options = {hash:{},data:data};
  stack2 = ((stack1 = helpers.imageBasedOnValueIsEmptyOrNot || depth0.imageBasedOnValueIsEmptyOrNot),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.contactNumber), options) : helperMissing.call(depth0, "imageBasedOnValueIsEmptyOrNot", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.contactNumber), options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n                    </div>\n                    <div class=\"span6 big-text-black\">\n                        Contact No.\n                    </div>\n                    <div class=\"span4 meta-summary-text-with-margin\">\n                        ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatText || depth0.formatText),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.contactNumber), options) : helperMissing.call(depth0, "formatText", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.contactNumber), options)))
    + "\n                    </div>\n                </div>\n\n                <div class=\"divider\"></div>\n\n                <div class=\"row-fluid beneficiary-detail-component\">\n                    <div class=\"span2\">\n                        ";
  options = {hash:{},data:data};
  stack2 = ((stack1 = helpers.imageBasedOnValueIsEmptyOrNot || depth0.imageBasedOnValueIsEmptyOrNot),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.ashaPhoneNumber), options) : helperMissing.call(depth0, "imageBasedOnValueIsEmptyOrNot", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.ashaPhoneNumber), options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n                    </div>\n                    <div class=\"span6 big-text-black\">\n                        ASHA\n                    </div>\n                    <div class=\"span4 meta-summary-text-with-margin\">\n                        ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatText || depth0.formatText),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.ashaPhoneNumber), options) : helperMissing.call(depth0, "formatText", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.ashaPhoneNumber), options)))
    + "\n                    </div>\n                </div>\n            </div>\n        </div>\n    </div>\n</div>\n";
  return buffer;
  }
function program2(depth0,data) {
  
  var buffer = "", stack1, stack2, options;
  buffer += "\n                    <div class=\"row-fluid beneficiary-detail-component\">\n                        <div class=\"span2\">\n                            ";
  options = {hash:{},data:data};
  stack2 = ((stack1 = helpers.imageBasedOnYesOrNo || depth0.imageBasedOnYesOrNo),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.isHighRiskStatusReviewed), options) : helperMissing.call(depth0, "imageBasedOnYesOrNo", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.isHighRiskStatusReviewed), options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n                        </div>\n                        <div class=\"span6 big-text-black\">\n                            Reviewed HRP Status\n                        </div>\n                        <div class=\"span4 meta-summary-text-with-margin\">\n                            ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatBooleanToYesOrNo || depth0.formatBooleanToYesOrNo),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.isHighRiskStatusReviewed), options) : helperMissing.call(depth0, "formatBooleanToYesOrNo", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.isHighRiskStatusReviewed), options)))
    + "\n                        </div>\n                    </div>\n\n                    <div class=\"divider\"></div>\n                ";
  return buffer;
  }

function program4(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\n<div class=\"registry-container container-fluid\">\n    <div class=\"big-text row-fluid detail-section-header\">\n        Delivery Plan\n        <div class=\"edit-birth-plan btn btn-primary pull-right\" data-form=\"BIRTH_PLANNING\" data-caseid=\"";
  if (stack1 = helpers.caseId) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.caseId; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "\">Edit</div>\n    </div>\n    <div class=\"well well-for-beneficiary-details-no-padding\">\n        <div class=\"container-fluid container-no-padding\">\n            <div class=\"delivery-plan\">\n                <div class=\"row-fluid beneficiary-detail-component\">\n                    <div class=\"span2\">\n                        <img class=\"no\"/>\n                    </div>\n                    <div class=\"span10\" style=\"\">Please discuss delivery plan</div>\n                </div>\n            </div>\n        </div>\n    </div>\n</div>\n";
  return buffer;
  }

  options = {hash:{},inverse:self.program(4, program4, data),fn:self.program(1, program1, data),data:data};
  stack2 = ((stack1 = helpers.shouldDisplayBirthPlan || depth0.shouldDisplayBirthPlan),stack1 ? stack1.call(depth0, depth0, options) : helperMissing.call(depth0, "shouldDisplayBirthPlan", depth0, options));
  if(stack2 || stack2 === 0) { return stack2; }
  else { return ''; }
  });
templates['child_detail'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, stack2, options, functionType="function", escapeExpression=this.escapeExpression, helperMissing=helpers.helperMissing, self=this;

function program1(depth0,data) {
  
  
  return "\n        <span class=\"right-text high-risk\">High Risk</span>\n        ";
  }

function program3(depth0,data) {
  
  
  return "\n        <span class=\"right-text normal-risk\">Normal Risk</span>\n        ";
  }

function program5(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "EC No: "
    + escapeExpression(((stack1 = ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.ecNumber)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + ", ";
  return buffer;
  }

function program7(depth0,data) {
  
  
  return "Out of area, ";
  }

function program9(depth0,data) {
  
  
  return "Boy";
  }

function program11(depth0,data) {
  
  
  return "Girl";
  }

function program13(depth0,data) {
  
  var buffer = "", stack1, options;
  buffer += "\n                        <div class=\"big-text high-risk\">\n                            <i class=\"icon-circle-arrow-up priority-indicator-icon\"></i> High Risk</span>\n                        </div>\n                        <div class=\"beneficiary-detail-footer risk-detail-text\">\n                            <ul>";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.camelCaseAndConvertToListItems || depth0.camelCaseAndConvertToListItems),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.highRiskReasonChild), options) : helperMissing.call(depth0, "camelCaseAndConvertToListItems", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.highRiskReasonChild), options)))
    + "</ul>\n                        </div>\n                        ";
  return buffer;
  }

function program15(depth0,data) {
  
  
  return "\n                        <div class=\"big-text normal-risk\">Normal Risk</div>\n                        ";
  }

function program17(depth0,data) {
  
  var buffer = "", stack1, options;
  buffer += "\n                    <div class=\"divider\"></div>\n                    <div class=\"beneficiary-detail-content\">\n                        <div class=\"row-fluid beneficiary-detail-component\">\n                            <span class=\"big-text\">Growth monitoring</span>\n                            <div class=\"summary-text\">"
    + escapeExpression(((stack1 = ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.childWeight)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + " kg on ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatDate || depth0.formatDate),stack1 ? stack1.call(depth0, ((stack1 = depth0.childDetails),stack1 == null || stack1 === false ? stack1 : stack1.dateOfBirth), options) : helperMissing.call(depth0, "formatDate", ((stack1 = depth0.childDetails),stack1 == null || stack1 === false ? stack1 : stack1.dateOfBirth), options)))
    + "</div>\n                        </div>\n                    </div>\n                    ";
  return buffer;
  }

function program19(depth0,data) {
  
  
  return "\n                    <div class=\"row-fluid unavailable-data\">\n                        No medical history data available\n                    </div>\n                    ";
  }

function program21(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\n                    <div class=\"row-fluid beneficiary-detail-component\">\n                        <div class=\"beneficiary-detail-content\">\n                            <div class=\"timeline\">\n                                ";
  stack1 = helpers.each.call(depth0, depth0.timelineEvents, {hash:{},inverse:self.noop,fn:self.program(22, program22, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n                            </div>\n                            <div class=\"container-section-component row-fluid show-more-button\">\n                                <div class=\"divider\"></div>\n                                <div class=\"expand\"> Show more</div>\n                            </div>\n                        </div>\n                    </div>\n                    ";
  return buffer;
  }
function program22(depth0,data) {
  
  var buffer = "", stack1, stack2;
  buffer += "\n                                <div class=\"timeline-component row-fluid timelineEvent\">\n                                    <div class=\"span3 type "
    + escapeExpression(((stack1 = depth0.type),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "\"></div>\n                                    <div class=\"span5\">\n                                        <strong>";
  if (stack2 = helpers.title) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.title; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "</strong><br>\n                                        ";
  stack2 = helpers.each.call(depth0, depth0.details, {hash:{},inverse:self.noop,fn:self.program(23, program23, data),data:data});
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n                                    </div>\n                                    <div class=\"span4 pull-right text-right\">\n                                        ";
  if (stack2 = helpers.date) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.date; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "\n                                    </div>\n                                </div>\n                                ";
  return buffer;
  }
function program23(depth0,data) {
  
  var buffer = "", stack1, stack2, options;
  buffer += "\n                                        ";
  options = {hash:{},data:data};
  stack2 = ((stack1 = helpers.formatText || depth0.formatText),stack1 ? stack1.call(depth0, depth0, options) : helperMissing.call(depth0, "formatText", depth0, options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "<br>\n                                        ";
  return buffer;
  }

  buffer += "<div class=\"navbar navbar-fixed-top\">\n    <div class=\"navbar-inner\">\n        <ul class=\"area-6_5 nav pull-left\">\n            <li class=\"area-7_5\">\n                <div class=\"navbar-header ellipsis\">B/O ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatText || depth0.formatText),stack1 ? stack1.call(depth0, ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.wifeName), options) : helperMissing.call(depth0, "formatText", ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.wifeName), options)))
    + "</div>\n            </li>\n        </ul>\n\n        <ul class=\"nav pull-right\">\n            <li><img class=\"separator\"></li>\n            <li data-modal-target=\"#childFormModal\"><img class=\"update-data\"></li>\n        </ul>\n    </div>\n</div>\n\n<div id=\"childFormModal\" class=\"modal-content\">\n    <div class=\"modal-row\" data-form=\"child_immunizations\" data-caseid=\"";
  if (stack2 = helpers.caseId) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.caseId; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "\">Child Immunizations</div>\n    <div class=\"divider\"></div>\n    <div class=\"modal-row\" data-form=\"child_close\" data-caseid=\"";
  if (stack2 = helpers.caseId) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.caseId; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "\">Close Child</div>\n</div>\n\n<div id=\"content\" class=\"content-detail\">\n    <div class=\"status-bar\">\n        <span class=\"left-text\">Child</span>\n        ";
  options = {hash:{},inverse:self.program(3, program3, data),fn:self.program(1, program1, data),data:data};
  stack2 = ((stack1 = helpers.ifFalse || depth0.ifFalse),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.isChildHighRisk), options) : helperMissing.call(depth0, "ifFalse", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.isChildHighRisk), options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n    </div>\n    <div class=\"registry-container container-fluid\">\n        <div class=\"row-fluid\">\n            <div class=\"span12\">\n                <ul class=\"client-details unstyled\">\n                    <li class=\"big-text\">B/O ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatText || depth0.formatText),stack1 ? stack1.call(depth0, ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.wifeName), options) : helperMissing.call(depth0, "formatText", ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.wifeName), options)))
    + "</li>\n                    <li class=\"medium-text spacing-below\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.capitalize || depth0.capitalize),stack1 ? stack1.call(depth0, ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.husbandName), options) : helperMissing.call(depth0, "capitalize", ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.husbandName), options)))
    + "</li>\n                    <li>";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatText || depth0.formatText),stack1 ? stack1.call(depth0, ((stack1 = depth0.location),stack1 == null || stack1 === false ? stack1 : stack1.villageName), options) : helperMissing.call(depth0, "formatText", ((stack1 = depth0.location),stack1 == null || stack1 === false ? stack1 : stack1.villageName), options)))
    + "</li>\n                    <li class=\"light-text\">";
  stack2 = helpers['if'].call(depth0, ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.isInArea), {hash:{},inverse:self.program(7, program7, data),fn:self.program(5, program5, data),data:data});
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "Thayi No: ";
  if (stack2 = helpers.thayiCardNumber) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.thayiCardNumber; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "</li>\n                </ul>\n            </div>\n        </div>\n    </div>\n    <div class=\"divider\"></div>\n    <div class=\"registry-container container-fluid\">\n        <div class=\"row-fluid\">\n            <div class=\"span4\">\n                <img id=\"childPhoto\" src=\"file://";
  if (stack2 = helpers.photo_path) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.photo_path; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "\">\n            </div>\n            <div class=\"span8\">\n                <ul class=\"client-details unstyled\">\n                    <li class=\"big-text-margin\">";
  options = {hash:{},inverse:self.program(11, program11, data),fn:self.program(9, program9, data),data:data};
  stack2 = ((stack1 = helpers.ifequal || depth0.ifequal),stack1 ? stack1.call(depth0, ((stack1 = depth0.childDetails),stack1 == null || stack1 === false ? stack1 : stack1.gender), "male", options) : helperMissing.call(depth0, "ifequal", ((stack1 = depth0.childDetails),stack1 == null || stack1 === false ? stack1 : stack1.gender), "male", options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "</li>\n                    <li class=\"text-blue\">Infant</li>\n                </ul>\n            </div>\n        </div>\n    </div>\n\n    <div class=\"registry-container container-fluid\">\n        <div class=\"big-text row-fluid detail-section-header\">\n            Summary\n        </div>\n        <div class=\"well well-for-beneficiary-details-no-padding\">\n            <div class=\"container-fluid container-no-padding\">\n                <div class=\"row-fluid beneficiary-detail-component\">\n                    <div class=\"beneficiary-detail-content\">\n                        ";
  options = {hash:{},inverse:self.program(15, program15, data),fn:self.program(13, program13, data),data:data};
  stack2 = ((stack1 = helpers.ifFalse || depth0.ifFalse),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.isChildHighRisk), options) : helperMissing.call(depth0, "ifFalse", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.isChildHighRisk), options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n                    </div>\n                    <div class=\"divider\"></div>\n                    <div class=\"beneficiary-detail-content\">\n                        <div class=\"span6 text-center right-border padded\">\n                            <span class=\"big-text\">"
    + escapeExpression(((stack1 = ((stack1 = depth0.childDetails),stack1 == null || stack1 === false ? stack1 : stack1.age)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</span><br><span class=\"meta-summary-text\">Age</span>\n                        </div>\n                        <div class=\"span6 text-center padded\">\n                            <span class=\"big-text\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatDate || depth0.formatDate),stack1 ? stack1.call(depth0, ((stack1 = depth0.childDetails),stack1 == null || stack1 === false ? stack1 : stack1.dateOfBirth), options) : helperMissing.call(depth0, "formatDate", ((stack1 = depth0.childDetails),stack1 == null || stack1 === false ? stack1 : stack1.dateOfBirth), options)))
    + "</span><br><span class=\"meta-summary-text\">Date of Birth</span>\n                        </div>\n                    </div>\n                    ";
  options = {hash:{},inverse:self.noop,fn:self.program(17, program17, data),data:data};
  stack2 = ((stack1 = helpers.ifFalse || depth0.ifFalse),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.childWeight), options) : helperMissing.call(depth0, "ifFalse", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.childWeight), options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n                </div>\n            </div>\n        </div>\n\n        <div class=\"registry-container container-fluid\">\n            <div class=\"big-text row-fluid detail-section-header\">\n                Timeline\n            </div>\n            <div class=\"well well-for-beneficiary-details-no-padding\">\n                <div class=\"container-fluid container-no-padding\">\n                    ";
  options = {hash:{},inverse:self.program(21, program21, data),fn:self.program(19, program19, data),data:data};
  stack2 = ((stack1 = helpers.ifequal || depth0.ifequal),stack1 ? stack1.call(depth0, ((stack1 = depth0.timelineEvents),stack1 == null || stack1 === false ? stack1 : stack1.length), 0, options) : helperMissing.call(depth0, "ifequal", ((stack1 = depth0.timelineEvents),stack1 == null || stack1 === false ? stack1 : stack1.length), 0, options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n                </div>\n            </div>\n        </div>\n    </div>\n</div>\n";
  return buffer;
  });
templates['ec_detail'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, stack2, options, helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression, functionType="function", self=this;

function program1(depth0,data) {
  
  
  return "\n        <span class=\"right-text high-risk\">High Priority</span>\n        ";
  }

function program3(depth0,data) {
  
  
  return "\n        <span class=\"right-text normal-risk\">Normal Priority</span>\n        ";
  }

function program5(depth0,data) {
  
  var buffer = "", stack1, options;
  buffer += "\n                        <div class=\"big-text high-risk\">\n                            <i class=\"icon-circle-arrow-up priority-indicator-icon\"></i> High Priority</span>\n                        </div>\n                        <div class=\"beneficiary-detail-footer risk-detail-text\">\n                            <ul>";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.camelCaseAndConvertToListItems || depth0.camelCaseAndConvertToListItems),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.highPriorityReason), options) : helperMissing.call(depth0, "camelCaseAndConvertToListItems", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.highPriorityReason), options)))
    + "</ul>\n                        </div>\n                        ";
  return buffer;
  }

function program7(depth0,data) {
  
  
  return "\n\n\n                        <div class=\"text-center big-text normal-risk\">Normal Priority</div>\n                        ";
  }

function program9(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\n                    <div class=\"divider divider-padding-bottom\"></div>\n                    <div class=\"beneficiary-detail-header\">Children</div>\n                    <div class=\"beneficiary-detail-content\">\n                        ";
  stack1 = helpers.each.call(depth0, depth0.children, {hash:{},inverse:self.noop,fn:self.program(10, program10, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n                    </div>\n                    ";
  return buffer;
  }
function program10(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\n                        ";
  stack1 = helpers['if'].call(depth0, depth0.isFemale, {hash:{},inverse:self.program(13, program13, data),fn:self.program(11, program11, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n                        ";
  return buffer;
  }
function program11(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\n                        <div class=\"ec-child girl\">\n                            <div>\n                                <span class=\"gender-text\">Girl</span><br>\n                                ";
  if (stack1 = helpers.age) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.age; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "\n                            </div>\n                        </div>\n                        ";
  return buffer;
  }

function program13(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\n                        <div class=\"ec-child boy\">\n                            <div>\n                                <span class=\"gender-text\">Boy</span><br>\n                                ";
  if (stack1 = helpers.age) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.age; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "\n                            </div>\n                        </div>\n                        ";
  return buffer;
  }

function program15(depth0,data) {
  
  
  return "\n                        <i class=\"icon-remove\"></i><span class=\"family-planning-label\">Current</span><span class=\"family-planning-current\">None</span><br/>\n                        ";
  }

function program17(depth0,data) {
  
  var buffer = "", stack1, options;
  buffer += "\n                        <i class=\"icon-ok\"></i><span class=\"family-planning-label\">Current</span><span class=\"family-planning-current\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.friendlyFPName || depth0.friendlyFPName),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.currentMethod), options) : helperMissing.call(depth0, "friendlyFPName", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.currentMethod), options)))
    + "</span><br/>\n                        ";
  return buffer;
  }

function program19(depth0,data) {
  
  
  return "\n                <div class=\"row-fluid unavailable-data\">\n                    No medical history data available\n                </div>\n                ";
  }

function program21(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\n                <div class=\"row-fluid beneficiary-detail-component\">\n                    <div class=\"beneficiary-detail-content\">\n                        <div class=\"timeline\">\n                            ";
  stack1 = helpers.each.call(depth0, depth0.timelineEvents, {hash:{},inverse:self.noop,fn:self.program(22, program22, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n                        </div>\n                        <div class=\"container-section-component row-fluid show-more-button\">\n                            <div class=\"divider\"></div>\n                            <div class=\"expand\"> Show more</div>\n                        </div>\n                    </div>\n                </div>\n                ";
  return buffer;
  }
function program22(depth0,data) {
  
  var buffer = "", stack1, stack2;
  buffer += "\n                            <div class=\"timeline-component row-fluid timelineEvent\">\n                                <div class=\"span3 type "
    + escapeExpression(((stack1 = depth0.type),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "\"></div>\n                                <div class=\"span5\">\n                                    <strong>";
  if (stack2 = helpers.title) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.title; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "</strong><br>\n                                    ";
  stack2 = helpers.each.call(depth0, depth0.details, {hash:{},inverse:self.noop,fn:self.program(23, program23, data),data:data});
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n                                </div>\n                                <div class=\"span4 pull-right text-right\">\n                                    ";
  if (stack2 = helpers.date) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.date; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "\n                                </div>\n                            </div>\n                            ";
  return buffer;
  }
function program23(depth0,data) {
  
  var buffer = "", stack1, stack2, options;
  buffer += "\n                                    ";
  options = {hash:{},data:data};
  stack2 = ((stack1 = helpers.formatText || depth0.formatText),stack1 ? stack1.call(depth0, depth0, options) : helperMissing.call(depth0, "formatText", depth0, options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "<br>\n                                    ";
  return buffer;
  }

  buffer += "<div class=\"navbar navbar-fixed-top\">\n    <div class=\"navbar-inner\">\n        <ul class=\"area-6_5 nav pull-left\">\n            <li class=\"area-7_5\">\n                <div class=\"navbar-header ellipsis\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatText || depth0.formatText),stack1 ? stack1.call(depth0, ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.wifeName), options) : helperMissing.call(depth0, "formatText", ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.wifeName), options)))
    + "</div>\n            </li>\n        </ul>\n\n        <ul class=\"nav pull-right\">\n            <li><img class=\"separator\"></li>\n            <li data-modal-target=\"#ecFormModal\"><img class=\"update-data\"></li>\n        </ul>\n    </div>\n</div>\n\n<div id=\"ecFormModal\" class=\"modal-content\">\n    <div class=\"modal-row\" data-form=\"anc_registration\" data-caseid=\"";
  if (stack2 = helpers.caseId) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.caseId; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "\">Register as ANC</div>\n    <div class=\"divider\"></div>\n    <div class=\"modal-row\" data-form=\"child_registration_ec\" data-caseid=\"";
  if (stack2 = helpers.caseId) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.caseId; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "\">Register a child</div>\n    <div class=\"divider\"></div>\n    <div class=\"modal-row\" data-form=\"ec_close\" data-caseid=\"";
  if (stack2 = helpers.caseId) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.caseId; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "\">Close EC</div>\n</div>\n\n<div id=\"content\" class=\"content-detail\">\n    <div class=\"status-bar\">\n        <span class=\"left-text\">EC</span>\n        ";
  options = {hash:{},inverse:self.program(3, program3, data),fn:self.program(1, program1, data),data:data};
  stack2 = ((stack1 = helpers.ifFalse || depth0.ifFalse),stack1 ? stack1.call(depth0, depth0.isHighPriority, options) : helperMissing.call(depth0, "ifFalse", depth0.isHighPriority, options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n    </div>\n    <div class=\"registry-container container-fluid\">\n        <div class=\"row-fluid\">\n            <div class=\"span4\">\n                <img id=\"womanPhoto\" src=\"file://";
  if (stack2 = helpers.photoPath) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.photoPath; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "\">\n            </div>\n            <div class=\"span8\">\n                <ul class=\"client-details unstyled\">\n                    <li>\n                        <div class=\"row-fluid\">\n                            <div class=\"span9 big-text\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatText || depth0.formatText),stack1 ? stack1.call(depth0, ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.wifeName), options) : helperMissing.call(depth0, "formatText", ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.wifeName), options)))
    + "</div>\n                            <div class=\"span3 social-vulnerability text-right\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatSocialVulnerability || depth0.formatSocialVulnerability),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.caste), ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.economicStatus), options) : helperMissing.call(depth0, "formatSocialVulnerability", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.caste), ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.economicStatus), options)))
    + "</div>\n                        </div>\n                    </li>\n                    <li class=\"medium-text spacing-below\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.capitalize || depth0.capitalize),stack1 ? stack1.call(depth0, ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.husbandName), options) : helperMissing.call(depth0, "capitalize", ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.husbandName), options)))
    + "</li>\n                    <li>";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatText || depth0.formatText),stack1 ? stack1.call(depth0, depth0.village, options) : helperMissing.call(depth0, "formatText", depth0.village, options)))
    + "</li>\n                    <li class=\"light-text\">EC No: ";
  if (stack2 = helpers.ecNumber) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.ecNumber; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "</li>\n                </ul>\n            </div>\n        </div>\n    </div>\n\n    <div class=\"registry-container container-fluid\">\n        <div class=\"big-text row-fluid detail-section-header\">\n            Summary\n        </div>\n        <div class=\"well well-for-beneficiary-details-no-padding\">\n            <div class=\"container-fluid container-no-padding\">\n                <div class=\"row-fluid beneficiary-detail-component\">\n                    <div class=\"beneficiary-detail-content\">\n                        ";
  options = {hash:{},inverse:self.program(7, program7, data),fn:self.program(5, program5, data),data:data};
  stack2 = ((stack1 = helpers.ifFalse || depth0.ifFalse),stack1 ? stack1.call(depth0, depth0.isHighPriority, options) : helperMissing.call(depth0, "ifFalse", depth0.isHighPriority, options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n                    </div>\n                </div>\n            </div>\n            <div class=\"container-fluid container-no-padding\">\n                <div class=\"row-fluid beneficiary-detail-component\">\n                    ";
  options = {hash:{},inverse:self.noop,fn:self.program(9, program9, data),data:data};
  stack2 = ((stack1 = helpers.ifNotZero || depth0.ifNotZero),stack1 ? stack1.call(depth0, ((stack1 = depth0.children),stack1 == null || stack1 === false ? stack1 : stack1.length), options) : helperMissing.call(depth0, "ifNotZero", ((stack1 = depth0.children),stack1 == null || stack1 === false ? stack1 : stack1.length), options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n                </div>\n            </div>\n        </div>\n    </div>\n\n    <div class=\"registry-container container-fluid\">\n        <div class=\"big-text row-fluid detail-section-header\">\n            Family Planning\n        </div>\n        <div class=\"well well-for-beneficiary-details\">\n            <div class=\"container-fluid container-no-padding\">\n                <div class=\"row-fluid beneficiary-detail-component\">\n                    <div class=\"beneficiary-detail-content\">\n                        ";
  options = {hash:{},inverse:self.program(17, program17, data),fn:self.program(15, program15, data),data:data};
  stack2 = ((stack1 = helpers.ifequal || depth0.ifequal),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.currentMethod), "none", options) : helperMissing.call(depth0, "ifequal", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.currentMethod), "none", options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n                    </div>\n                </div>\n            </div>\n        </div>\n    </div>\n\n    <div class=\"registry-container container-fluid\">\n        <div class=\"big-text row-fluid detail-section-header\">\n            Timeline\n        </div>\n        <div class=\"well well-for-beneficiary-details-no-padding\">\n            <div class=\"container-fluid container-no-padding\">\n                ";
  options = {hash:{},inverse:self.program(21, program21, data),fn:self.program(19, program19, data),data:data};
  stack2 = ((stack1 = helpers.ifequal || depth0.ifequal),stack1 ? stack1.call(depth0, ((stack1 = depth0.timelineEvents),stack1 == null || stack1 === false ? stack1 : stack1.length), 0, options) : helperMissing.call(depth0, "ifequal", ((stack1 = depth0.timelineEvents),stack1 == null || stack1 === false ? stack1 : stack1.length), 0, options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n            </div>\n        </div>\n    </div>\n</div>\n";
  return buffer;
  });
templates['home'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); partials = this.merge(partials, Handlebars.partials); data = data || {};
  var buffer = "", stack1, options, self=this, helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;


  buffer += "<div class=\"row-fluid with-margin with-padding\" id=\"logo-and-buttons\">\n    <div class=\"span10\">\n        <div class=\"row-fluid with-margin\" id=\"drishti-logo\">\n            <div class=\"span10\">\n                <center><img src=\"../img/smart_registry/logo.png\"></center>\n            </div>\n        </div>\n        <div class=\"row-fluid with-margin\">\n            ";
  stack1 = self.invokePartial(partials.anm_navigation, 'anm_navigation', depth0, helpers, partials, data);
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n        </div>\n    </div>\n</div>\n<div class=\"row-fluid with-margin with-padding\" id=\"reports-videos-buttons\">\n    <div class=\"span5\">\n        <a class=\"btn btn-block btn-inverse btn-home\" id=\"reportsButton\"><i class=\"icon-reporting\"></i> ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.internationalize || depth0.internationalize),stack1 ? stack1.call(depth0, "home_report_label", options) : helperMissing.call(depth0, "internationalize", "home_report_label", options)))
    + "</a>\n    </div>\n    <div class=\"span5\">\n        <a class=\"btn btn-block btn-inverse btn-home\" id=\"videosButton\"><i class=\"icon-tv\"></i> ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.internationalize || depth0.internationalize),stack1 ? stack1.call(depth0, "home_videos_label", options) : helperMissing.call(depth0, "internationalize", "home_videos_label", options)))
    + "</a>\n    </div>\n</div>\n";
  return buffer;
  });
templates['pnc_detail'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, stack2, options, functionType="function", escapeExpression=this.escapeExpression, helperMissing=helpers.helperMissing, self=this;

function program1(depth0,data) {
  
  
  return "\n        <span class=\"left-text\">PNC</span>\n        ";
  }

function program3(depth0,data) {
  
  
  return "\n        <span class=\"left-text\">Out of Area PNC</span>\n        ";
  }

function program5(depth0,data) {
  
  
  return "\n        <span class=\"right-text high-risk\">High Risk</span>\n        ";
  }

function program7(depth0,data) {
  
  
  return "\n        <span class=\"right-text normal-risk\">Normal Risk</span>\n        ";
  }

function program9(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "EC No: "
    + escapeExpression(((stack1 = ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.ecNumber)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + ", ";
  return buffer;
  }

function program11(depth0,data) {
  
  
  return "Out\n                        of area, ";
  }

function program13(depth0,data) {
  
  var buffer = "", stack1, options;
  buffer += "\n                        <div class=\"big-text high-risk\">\n                            <i class=\"icon-circle-arrow-up priority-indicator-icon\"></i> High Risk</span>\n                        </div>\n                        <div class=\"beneficiary-detail-footer risk-detail-text\">\n                            <ul>";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.camelCaseAndConvertToListItems || depth0.camelCaseAndConvertToListItems),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.highRiskReason), options) : helperMissing.call(depth0, "camelCaseAndConvertToListItems", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.highRiskReason), options)))
    + "</ul>\n                        </div>\n                        ";
  return buffer;
  }

function program15(depth0,data) {
  
  
  return "\n                        <div class=\"text-center big-text normal-risk\">Normal Risk</div>\n                        ";
  }

function program17(depth0,data) {
  
  var buffer = "", stack1, options;
  buffer += "\n                    <div class=\"row-fluid beneficiary-detail-component\">\n                        <div class=\"divider\"></div>\n                    </div>\n                    <div class=\"row-fluid beneficiary-detail-component\">\n                        <div class=\"beneficiary-detail-content\">\n                            <b>Delivery Complications</b><br/>\n                            <ul>";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.camelCaseAndConvertToListItems || depth0.camelCaseAndConvertToListItems),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.deliveryComplications), options) : helperMissing.call(depth0, "camelCaseAndConvertToListItems", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.deliveryComplications), options)))
    + "</ul>\n                        </div>\n                    </div>\n                    ";
  return buffer;
  }

function program19(depth0,data) {
  
  
  return "\n                <div class=\"row-fluid unavailable-data\">\n                    No medical history data available\n                </div>\n                ";
  }

function program21(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\n                <div class=\"row-fluid beneficiary-detail-component\">\n                    <div class=\"beneficiary-detail-content\">\n                        <div class=\"timeline\">\n                            ";
  stack1 = helpers.each.call(depth0, depth0.timelineEvents, {hash:{},inverse:self.noop,fn:self.program(22, program22, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n                        </div>\n                        <div class=\"container-section-component row-fluid show-more-button\">\n                            <div class=\"divider\"></div>\n                            <div class=\"expand\"> Show more</div>\n                        </div>\n                    </div>\n                </div>\n                ";
  return buffer;
  }
function program22(depth0,data) {
  
  var buffer = "", stack1, stack2;
  buffer += "\n                            <div class=\"timeline-component row-fluid timelineEvent\">\n                                <div class=\"span3 type "
    + escapeExpression(((stack1 = depth0.type),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "\"></div>\n                                <div class=\"span5\">\n                                    <strong>";
  if (stack2 = helpers.title) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.title; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "</strong><br>\n                                    ";
  stack2 = helpers.each.call(depth0, depth0.details, {hash:{},inverse:self.noop,fn:self.program(23, program23, data),data:data});
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n                                </div>\n                                <div class=\"span4 pull-right text-right\">\n                                    ";
  if (stack2 = helpers.date) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.date; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "\n                                </div>\n                            </div>\n                            ";
  return buffer;
  }
function program23(depth0,data) {
  
  var buffer = "", stack1, stack2, options;
  buffer += "\n                                    ";
  options = {hash:{},data:data};
  stack2 = ((stack1 = helpers.formatText || depth0.formatText),stack1 ? stack1.call(depth0, depth0, options) : helperMissing.call(depth0, "formatText", depth0, options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "<br>\n                                    ";
  return buffer;
  }

  buffer += "<div class=\"navbar navbar-fixed-top\">\n    <div class=\"navbar-inner\">\n        <ul class=\"area-6_5 nav pull-left\">\n            <li class=\"area-7_5\">\n                <div class=\"navbar-header ellipsis\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatText || depth0.formatText),stack1 ? stack1.call(depth0, ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.wifeName), options) : helperMissing.call(depth0, "formatText", ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.wifeName), options)))
    + "</div>\n            </li>\n        </ul>\n\n        <ul class=\"nav pull-right\">\n            <li><img class=\"separator\"></li>\n            <li data-modal-target=\"#pncFormModal\"><img class=\"update-data\"></li>\n        </ul>\n    </div>\n</div>\n\n<div id=\"pncFormModal\" class=\"modal-content\">\n    <div class=\"modal-row\" data-form=\"pnc_visit\" data-caseid=\"";
  if (stack2 = helpers.caseId) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.caseId; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "\">PNC visit</div>\n    <div class=\"divider\"></div>\n    <div class=\"modal-row\" data-form=\"pnc_close\" data-caseid=\"";
  if (stack2 = helpers.caseId) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.caseId; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "\">Close PNC</div>\n</div>\n\n<div id=\"content\" class=\"content-detail\">\n    <div class=\"status-bar\">\n        ";
  stack2 = helpers['if'].call(depth0, ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.isInArea), {hash:{},inverse:self.program(3, program3, data),fn:self.program(1, program1, data),data:data});
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n        ";
  options = {hash:{},inverse:self.program(7, program7, data),fn:self.program(5, program5, data),data:data};
  stack2 = ((stack1 = helpers.ifFalse || depth0.ifFalse),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.isHighRisk), options) : helperMissing.call(depth0, "ifFalse", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.isHighRisk), options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n    </div>\n    <div class=\"registry-container container-fluid\">\n        <div class=\"row-fluid\">\n            <div class=\"span4\">\n                <img id=\"womanPhoto\" src=\"file://"
    + escapeExpression(((stack1 = ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.photo_path)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "\">\n            </div>\n            <div class=\"span8\">\n                <ul class=\"client-details unstyled\">\n                    <li>\n                        <div class=\"row-fluid\">\n                            <div class=\"span9 big-text\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatText || depth0.formatText),stack1 ? stack1.call(depth0, ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.wifeName), options) : helperMissing.call(depth0, "formatText", ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.wifeName), options)))
    + "</div>\n                            <div class=\"span3 social-vulnerability text-right\">\n                                ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatSocialVulnerability || depth0.formatSocialVulnerability),stack1 ? stack1.call(depth0, ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.caste), ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.economicStatus), options) : helperMissing.call(depth0, "formatSocialVulnerability", ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.caste), ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.economicStatus), options)))
    + "\n                            </div>\n                        </div>\n                    </li>\n                    <li class=\"medium-text spacing-below\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.capitalize || depth0.capitalize),stack1 ? stack1.call(depth0, ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.husbandName), options) : helperMissing.call(depth0, "capitalize", ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.husbandName), options)))
    + "</li>\n                    <li>";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatText || depth0.formatText),stack1 ? stack1.call(depth0, ((stack1 = depth0.location),stack1 == null || stack1 === false ? stack1 : stack1.villageName), options) : helperMissing.call(depth0, "formatText", ((stack1 = depth0.location),stack1 == null || stack1 === false ? stack1 : stack1.villageName), options)))
    + "</li>\n                    <li class=\"light-text\">";
  stack2 = helpers['if'].call(depth0, ((stack1 = depth0.coupleDetails),stack1 == null || stack1 === false ? stack1 : stack1.isInArea), {hash:{},inverse:self.program(11, program11, data),fn:self.program(9, program9, data),data:data});
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "Thayi No: ";
  if (stack2 = helpers.thayiCardNumber) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.thayiCardNumber; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "\n                    </li>\n                </ul>\n            </div>\n        </div>\n    </div>\n\n    <div class=\"registry-container container-fluid\">\n        <div class=\"big-text row-fluid detail-section-header\">\n            Summary\n        </div>\n        <div class=\"well well-for-beneficiary-details-no-padding\">\n            <div class=\"container-fluid container-no-padding\">\n                <div class=\"row-fluid beneficiary-detail-component\">\n                    <div class=\"beneficiary-detail-content\">\n                        ";
  options = {hash:{},inverse:self.program(15, program15, data),fn:self.program(13, program13, data),data:data};
  stack2 = ((stack1 = helpers.ifFalse || depth0.ifFalse),stack1 ? stack1.call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.isHighRisk), options) : helperMissing.call(depth0, "ifFalse", ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.isHighRisk), options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n                    </div>\n                    <div class=\"divider\"></div>\n                    <div class=\"beneficiary-detail-content\">\n                        <div class=\"span6 text-center right-border padded\">\n                            <span class=\"big-text\">"
    + escapeExpression(((stack1 = ((stack1 = depth0.pncDetails),stack1 == null || stack1 === false ? stack1 : stack1.daysPostpartum)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</span><br><span\n                                class=\"meta-summary-text\">Days Postpartum</span>\n                        </div>\n                        <div class=\"span6 text-center padded\">\n                            <span class=\"big-text\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatDate || depth0.formatDate),stack1 ? stack1.call(depth0, ((stack1 = depth0.pncDetails),stack1 == null || stack1 === false ? stack1 : stack1.dateOfDelivery), options) : helperMissing.call(depth0, "formatDate", ((stack1 = depth0.pncDetails),stack1 == null || stack1 === false ? stack1 : stack1.dateOfDelivery), options)))
    + "</span><br><span\n                                class=\"meta-summary-text\">Date of Delivery</span>\n                        </div>\n                    </div>\n                    ";
  stack2 = helpers['if'].call(depth0, ((stack1 = depth0.details),stack1 == null || stack1 === false ? stack1 : stack1.deliveryComplications), {hash:{},inverse:self.noop,fn:self.program(17, program17, data),data:data});
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n                </div>\n            </div>\n        </div>\n    </div>\n\n    <div class=\"registry-container container-fluid\">\n        <div class=\"big-text row-fluid detail-section-header\">\n            Timeline\n        </div>\n        <div class=\"well well-for-beneficiary-details-no-padding\">\n            <div class=\"container-fluid container-no-padding\">\n                ";
  options = {hash:{},inverse:self.program(21, program21, data),fn:self.program(19, program19, data),data:data};
  stack2 = ((stack1 = helpers.ifequal || depth0.ifequal),stack1 ? stack1.call(depth0, ((stack1 = depth0.timelineEvents),stack1 == null || stack1 === false ? stack1 : stack1.length), 0, options) : helperMissing.call(depth0, "ifequal", ((stack1 = depth0.timelineEvents),stack1 == null || stack1 === false ? stack1 : stack1.length), 0, options));
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n            </div>\n        </div>\n    </div>\n</div>\n";
  return buffer;
  });
templates['report_indicator_case_list'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, stack2, options, functionType="function", escapeExpression=this.escapeExpression, helperMissing=helpers.helperMissing, self=this;

function program1(depth0,data) {
  
  var buffer = "", stack1, stack2, options;
  buffer += "\n        <div class=\"row-fluid\">\n            <a data-caseid=";
  if (stack1 = helpers.caseId) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.caseId; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + " class=\"client-list-item\">\n                <div class=\"span8\">\n                    <ul class=\"client-details unstyled\">\n                        <li class=\"big-text\">\n                            ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.capitalize || depth0.capitalize),stack1 ? stack1.call(depth0, depth0.womanName, options) : helperMissing.call(depth0, "capitalize", depth0.womanName, options)))
    + "\n                            ";
  stack2 = helpers['if'].call(depth0, depth0.isHighRisk, {hash:{},inverse:self.noop,fn:self.program(2, program2, data),data:data});
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n                        </li>\n                        <li>";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.capitalize || depth0.capitalize),stack1 ? stack1.call(depth0, depth0.husbandName, options) : helperMissing.call(depth0, "capitalize", depth0.husbandName, options)))
    + "</li>\n                        <li>\n                            ";
  stack2 = helpers['if'].call(depth0, depth0.thayiCardNumber, {hash:{},inverse:self.noop,fn:self.program(4, program4, data),data:data});
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += " ";
  stack2 = helpers['if'].call(depth0, depth0.ecNumber, {hash:{},inverse:self.noop,fn:self.program(6, program6, data),data:data});
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n                        </li>\n                    </ul>\n                </div>\n\n                <div class=\"span4 pull-text-completely-right\">\n                    ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.formatText || depth0.formatText),stack1 ? stack1.call(depth0, depth0.villageName, options) : helperMissing.call(depth0, "formatText", depth0.villageName, options)))
    + "\n                </div>\n                <div class=\"divider divider-padding-both\"></div>\n            </a>\n        </div>\n        ";
  return buffer;
  }
function program2(depth0,data) {
  
  
  return "<span class=\"high-risk\"> HR </span>";
  }

function program4(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "Thayi No: ";
  if (stack1 = helpers.thayiCardNumber) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.thayiCardNumber; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1);
  return buffer;
  }

function program6(depth0,data) {
  
  var buffer = "", stack1;
  buffer += " EC No: ";
  if (stack1 = helpers.ecNumber) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.ecNumber; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1);
  return buffer;
  }

  buffer += "<div class=\"navbar navbar-fixed-top\">\n    <div class=\"navbar-inner\">\n        <ul class=\"nav pull-left\">\n            <li class=\"navbar-header\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.monthName || depth0.monthName),stack1 ? stack1.call(depth0, depth0.month, options) : helperMissing.call(depth0, "monthName", depth0.month, options)))
    + "</li>\n        </ul>\n    </div>\n</div>\n\n<div class=\"content\">\n    <div class=\"registry-container container-fluid\">\n        ";
  stack2 = helpers.each.call(depth0, depth0.beneficiaries, {hash:{},inverse:self.noop,fn:self.program(1, program1, data),data:data});
  if(stack2 || stack2 === 0) { buffer += stack2; }
  buffer += "\n    </div>\n</div>\n";
  return buffer;
  });
templates['report_indicator_detail'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression, helperMissing=helpers.helperMissing, self=this;

function program1(depth0,data,depth1) {
  
  var buffer = "", stack1, stack2, options;
  buffer += "\n            <tr class=\"indicator-monthly-report\" data-month=\"";
  if (stack1 = helpers.month) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.month; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "\">\n                <td>\n                    <div class=\"report-medium\">"
    + escapeExpression(((stack1 = depth1.annualTarget),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</div>\n                    <div class=\"meta-summary-text\">Target</div>\n                </td>\n                <td>\n                    <div class=\"report-medium text-blue\">";
  if (stack2 = helpers.currentProgress) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.currentProgress; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "</div>\n                    <div class=\"meta-summary-text\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.monthName || depth0.monthName),stack1 ? stack1.call(depth0, depth0.month, options) : helperMissing.call(depth0, "monthName", depth0.month, options)))
    + " '";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.shortYear || depth0.shortYear),stack1 ? stack1.call(depth0, depth0.year, options) : helperMissing.call(depth0, "shortYear", depth0.year, options)))
    + "</div>\n                </td>\n                <td>\n                    <div class=\"report-medium text-blue\">";
  if (stack2 = helpers.aggregatedProgress) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.aggregatedProgress; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "</div>\n                    <div class=\"meta-summary-text\">Total</div>\n                </td>\n                <td>\n                    <div class=\"report-medium text-green\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.percentage || depth0.percentage),stack1 ? stack1.call(depth0, depth0.aggregatedProgress, depth1.annualTarget, options) : helperMissing.call(depth0, "percentage", depth0.aggregatedProgress, depth1.annualTarget, options)))
    + "</div>\n                    <div class=\"meta-summary-text\">Percent</div>\n                </td>\n            </tr>\n            ";
  return buffer;
  }

  buffer += "<div class=\"navbar navbar-fixed-top\">\n    <div class=\"navbar-inner\">\n        <ul class=\"area-full nav pull-left\">\n            <li class=\"area-8_0\">\n                <div class=\"navbar-header ellipsis\">";
  if (stack1 = helpers.categoryDescription) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.categoryDescription; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</div>\n            </li>\n        </ul>\n    </div>\n</div>\n\n<div class=\"content\">\n    <div class=\"registry-container container-fluid\">\n        <div class=\"big-text-black\">";
  if (stack1 = helpers.description) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.description; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</div>\n        <table class=\"table table-bordered indicator-report\">\n            <tbody>\n            ";
  stack1 = helpers.each.call(depth0, depth0.monthlySummaries, {hash:{},inverse:self.noop,fn:self.programWithDepth(1, program1, data, depth0),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n            </tbody>\n        </table>\n    </div>\n</div>\n";
  return buffer;
  });
templates['report_indicator_list'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression, helperMissing=helpers.helperMissing, self=this;

function program1(depth0,data) {
  
  var buffer = "", stack1, stack2, options;
  buffer += "\n        <div class=\"big-text-black\">";
  if (stack1 = helpers.description) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.description; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</div>\n        <table class=\"table table-bordered indicator-report\" data-indicator=\"";
  if (stack1 = helpers.indicatorIdentifier) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.indicatorIdentifier; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "\">\n            <tbody>\n            <tr>\n                <td>\n                    <div class=\"report-medium\">";
  if (stack1 = helpers.annualTarget) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.annualTarget; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</div>\n                    <div class=\"meta-summary-text\">Annual Target</div>\n                </td>\n                <td>\n                    <div class=\"report-medium text-blue\">";
  if (stack1 = helpers.currentProgress) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.currentProgress; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</div>\n                    <div class=\"meta-summary-text\">in ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.monthName || depth0.monthName),stack1 ? stack1.call(depth0, depth0.currentMonth, options) : helperMissing.call(depth0, "monthName", depth0.currentMonth, options)))
    + "</div>\n                </td>\n                <td>\n                    <div class=\"report-medium text-blue\">";
  if (stack2 = helpers.aggregatedProgress) { stack2 = stack2.call(depth0, {hash:{},data:data}); }
  else { stack2 = depth0.aggregatedProgress; stack2 = typeof stack2 === functionType ? stack2.apply(depth0) : stack2; }
  buffer += escapeExpression(stack2)
    + "</div>\n                    <div class=\"meta-summary-text\">Total to ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.monthName || depth0.monthName),stack1 ? stack1.call(depth0, depth0.currentMonth, options) : helperMissing.call(depth0, "monthName", depth0.currentMonth, options)))
    + "</div>\n                </td>\n                <td>\n                    <div class=\"report-medium text-green\">";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.percentage || depth0.percentage),stack1 ? stack1.call(depth0, depth0.aggregatedProgress, depth0.annualTarget, options) : helperMissing.call(depth0, "percentage", depth0.aggregatedProgress, depth0.annualTarget, options)))
    + "</div>\n                    <div class=\"meta-summary-text\">Percent of Target</div>\n                </td>\n            </tr>\n            </tbody>\n        </table>\n        ";
  return buffer;
  }

  buffer += "<div class=\"navbar navbar-fixed-top\">\n    <div class=\"navbar-inner\">\n        <ul class=\"area-full nav pull-left\">\n            <li class=\"area-8_0\"><div class=\"navbar-header ellipsis\">";
  if (stack1 = helpers.description) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.description; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</div></li>\n        </ul>\n    </div>\n</div>\n\n<div class=\"content\">\n    <div class=\"registry-container container-fluid\">\n        ";
  stack1 = helpers.each.call(depth0, depth0.indicatorReports, {hash:{},inverse:self.noop,fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\n    </div>\n</div>\n";
  return buffer;
  });
})();