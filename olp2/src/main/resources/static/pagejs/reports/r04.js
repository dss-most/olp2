	
var currentFiscalYear;
Ext.onReady(function(){
	Ext.QuickTips.init();
	
	
	Ext.define('PlanActivity', {
		extend: 'Ext.data.Model',
		fields : [{name:'EXAMPLE_ID'},
		          {name:'ACTIVITY_ID'},
		          {name:'PLAN_ID'},
		          {name:'PLAN_CODE'},
		          {name:'BRANCH_NAME'},
		          {name:'EXAMPLE_CODE'},
		          {name:'EXAMPLE_NAME'},
		          {name:'ACTIVITY_CODE'},
		          {name:'ACTIVITY_NAME'},
		          {name:'PRICE', type: 'number'},
		          {name:'ROOM_NUMBER'},
		          {name: 'COMPANY_TH_APPLICANT'},
		          {name: 'COMPANY_EN_APPLICANT'},
		          {name: 'ADD_APPLICANT'},
		          {name: 'ADD_APPLICANT_1'},
		          {name: 'TAMBON_NAME'},
		          {name: 'AMPHUR_NAME'},
		          {name: 'PROVINCE_NAME'},
		          {name:'CLOSE_APPLICANT_DATE', type: 'date', dateFormat: 'c'},
		          {name:'START_ACTIVITY_DATE', type: 'date', dateFormat: 'c'},
		          {name:'EMP_NAME'},
		          {name:'ROUND'}]
	});
	
	Ext.define('Customer', {
		extend : 'Ext.data.Model',
		fields : ['ID', 'CUSTOMER_CODE', 'CUSTOMER_NAME_CANDIDATE']
	});
	
	Ext.define('Register', {
		extend : 'Ext.data.Model',
		fields : ['ID', 'REGISTER_NUMBER', 'CUSTOMER_CODE', 'CUSTOMER_NAME_CANDIDATE', 'ACTIVITIES']
	});


	
	var planActivityStore = new Ext.data.Store({
		model: 'PlanActivity',
		id: "store_planActivityStore",
		groupField: 'PLAN_ID',
		proxy: {
			type: 'ajax',
			url: appUrl('/json/listPlanActivity')
		}
	});
	
	var customerStore = new Ext.data.Store({
		model : 'Customer',
		id : 'store_customerStore',
		proxy : {
			type : 'ajax',
			url : appUrl('/json/listRegisterByActivtiy')
		}
	});

	var simpleForm = Ext.create('Ext.form.Panel', {
		id : 'mainForm',
		url : 'exportCustomerRegistration',
		frame : true,
		title : 'ข้อมูลการลงทะเบียนกิจกรรม',
		bodyStyle : 'padding:5px 5px 0',
		standardSubmit: true,
		width : 900,
		fieldDefaults : {
			msgTarget : 'side',
			labelWidth : 75
		},
		defaultType : 'textfield',
		defaults : {
			anchor : '100%'
		},

		items : [ {
			fieldLabel : 'ปีงบประมาณ',
			name : 'fiscalYear',
			id : 'text_fiscalYear',
			allowBlank : true,
			listeners: {
				change: function(field, newValue, oldValue, eOpts) {
					//now set url for customer Store
					Ext.getStore('store_planActivityStore').load({
						params: {
							fiscalYear: newValue
						}
					});
					currentFiscalYear = newValue;
					
				}
			}
		}, {
			fieldLabel : 'กิจกรรม',
			name : 'activityCombo',
			id : 'combo_activityCombo',
			xtype : 'combo',
			displayField: 'ACTIVITY_CODE',
			store : planActivityStore,
			valueField : 'ACTIVITY_ID',
			typeAhead : true,
			valueNotFoundText : "ไม่พบรหัสกิจกรรมนี้",
			minChars : 2,
			selectOnFocus : true,
			queryMode : 'local',
			displayTpl : 	'<tpl for=".">' +
		    					'{ACTIVITY_CODE} : {ACTIVITY_NAME}' +
		    					'<tpl if="xindex < xcount"> , </tpl>' +
		    				'</tpl>',
			listConfig: {
				emptyText: "<div style='padding:12px 8px 12px 8px; color:red;'><i class='fa fa-exclamation-circle'></i> ไม่พบรหัสกิจกรรมนี้ กรุณาระบุใหม่</div>",
//				getInnerTpl : function() {
//					return '<div> <b>{ACTIVITY_CODE}</b> : {ACTIVITY_NAME} </div>';
//				}
			},
	        tpl: '<ul class="x-list-plain">' +
		            '{% var lastBranch, branch; %}' +
		            '<tpl for=".">' +
		            	'{% branch = values.EXAMPLE_NAME;' +
		                // Only show region headers when there are more than 1 choices
		            		'if (branch !== lastBranch) {' + 
		                    	'lastBranch = branch;%}' +
		                    	'<li class="x-grid-group-hd x-grid-group-title">{EXAMPLE_NAME}</li>' +
		                '{%}%}'+
		                '<li class="x-boundlist-item">' +
		                    '<b>{ACTIVITY_CODE}</b> : {ACTIVITY_NAME}' +
		                '</li>'+
		            '</tpl>'+
		        '</ul>',
				
			listeners : {
				select : function(combo, records, eOpts) {
					var g = Ext.getCmp('grid_activity');
					var s = g.getStore();
					s.load({
						params: {
							fiscalYear: Ext.getCmp('text_fiscalYear').value,
							activityId: records.data.ACTIVITY_ID
						}
					});					
					
				}
//			,
//				change : function (oldValue, newValue) {
//					
//					if(newValue == null || newValue.length == 0) {
//						var g = Ext.getCmp('grid_activity');
//						var s = g.getStore();
//						s.load({
//							params: {
//								fiscalYear: Ext.getCmp('text_fiscalYear').value
//							}
//						});
//						s.group("BRANCH_NAME");
//					}
//				}
			
			}
		
//		},{
//			xtype      : 'fieldcontainer',
//            fieldLabel : 'ประเภท',
//            defaultType: 'radiofield',
//            defaults: {
//                width: 90
//            },
//            layout: 'hbox',
//            items: [
//                {
//                    boxLabel  : 'กิจกรรม',
//                    name      : 'activityType',
//                    inputValue: 'act',
//                    id        : 'radio1'
//                }, {
//                    boxLabel  : 'QC-Sample',
//                    name      : 'activityType',
//                    inputValue: 'qc',
//                    id        : 'radio2'
//                }, {
//                    boxLabel  : 'ทั้งหมด',
//                    name      : 'activityType',
//                    inputValue: 'all',
//                    id        : 'radio3'
//                }
//            ]
//	            
		}, {
			xtype: 'grid',
			id: 'grid_activity',
			store: customerStore,
			height: 450,
			columns: [
					{xtype: 'rownumberer',locked: true},
				 	{text: "เลขที่ลงทะเบียน", dataIndex: 'REGISTER_NUMBER', width: 100, locked: true},
				 	{text: "รหัส", dataIndex: 'CUSTOMER_CODE', width: 90,locked: true },
				 	{text: "ขื่อผู้สมัคร", dataIndex: 'CUSTOMER_NAME_CANDIDATE', cellWrap:true, width:160, locked:true},
				 	{text: "สถานะ", dataIndex: 'STATUS_REGIS_FORM', width: 60 },
			        {text: "รหัสลับ", dataIndex: 'C_PASSWORD', width: 90 },
				 	
				 	{text: "ตำแหน่ง", dataIndex: 'POSITION_CANDIDATE', cellWrap:true, width:180},
				 	{text: "เบอร์โทรศัพท์", dataIndex: 'TEL_NO', cellWrap:true, width:160},
				 	{text: "เบอร์มือถือ", dataIndex: 'PHON_NO', cellWrap:true, width:120},
				 	{text: "โทรสาร", dataIndex: 'FAX_NO', cellWrap:true, width:120},
				 	{text: "อีเมล์", dataIndex: 'EMAIL', cellWrap:true, width:200},
				 	{text: "อีเมล์ interim", dataIndex: 'OLP_APP_LOGIN', cellWrap:true, width:200},
				 	
				 	
				 	{text: "หน่วยงาน (ไทย)", dataIndex: 'COMPANY_TH_APPLICANT',  cellWrap:true, width: 200},
				 	{text: "หน่วยงาน (eng)", dataIndex: 'COMPANY_EN_APPLICANT',  cellWrap:true, width: 200},
					{text: "ที่อยู่", dataIndex: 'ADD_APPLICANT',  cellWrap:true, width: 200},
					{text: "ที่อยู่_1", dataIndex: 'ADD_APPLICANT_1',  cellWrap:true, width: 200},
					{text: "ตำบล", dataIndex: 'TAMBON_NAME',  cellWrap:true, width: 100},
					{text: "อำเภอ", dataIndex: 'AMPHUR_NAME',  cellWrap:true, width: 100},
					{text: "จังหวัด", dataIndex: 'PROVINCE_NAME',  cellWrap:true, width: 100},
				 	
			          
			         
			],
			frame:false,
			viewConfig: {
				emptyText: 'no data!'
			}
			
		}],

		buttons : [  {
			text: 'Export เฉพาะกิจกรรม',
			id: 'btn_export_activity',
			listeners : {
				click : function() {
					var form = this.up('form');
					
					
					form.getEl().dom.target = '_blank';
					
					var activityId = Ext.getCmp('combo_activityCombo').value;
					var fiscalYear = Ext.getCmp('text_fiscalYear').value;
					
					form.submit({
						target: '_blank',
						params: {
							activityId : activityId,
							fiscalYear: fiscalYear
						}
					});
					
				}
			}
		},{
			text : 'Export ทั้งปี ตามใบสมัครกิจกรรม' ,
			id : 'btn_exprot_all',
			listeners : {
				click : function() {
					
					var form = this.up('form');
					
					form.getEl().dom.target = '_blank';
					
					var fiscalYear = Ext.getCmp('text_fiscalYear').value;
					
					form.submit({
						target: '_blank',
						params: {
							activityId : null,
							fiscalYear: fiscalYear
						}
					});
				}
			}
		},{
			text : 'Export เฉพาะชื่อลูกค้า' ,
			id : 'btn_exprot_all_customer',
			listeners : {
				click : function() {
					
					var form = this.up('form');
					
					form.getEl().dom.target = '_blank';
					
					var fiscalYear = Ext.getCmp('text_fiscalYear').value;
					
					if(fiscalYear != null) {
						form.submit({
							target: '_blank',
							url: 'exportCustomerByFiscalYear',
							params: {
								fiscalYear: fiscalYear
							}
						});
					} else {
						form.submit({
							target: '_blank',
							url: 'exportAllCustomer',
							params: {
								
							}
						});
					}
					
					
					
				}
			}
		} ]
	});

	//var el = simpleForm.getEl();
	//el.dom.target = '_blank';
	simpleForm.render('formCanvas');

});