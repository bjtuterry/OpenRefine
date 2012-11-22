
function ZemantaCrowdFlowerDialog(onDone) {
  this._onDone = onDone;
  this._extension = {};
  this._existingJob = false;
  var dismissBusy = DialogSystem.showBusy();
  
  this._dialog = $(DOM.loadHTML("crowdsourcing", "scripts/dialogs/crowdflower-job-columns-dialog.html"));
  this._elmts = DOM.bind(this._dialog);
  this._elmts.dialogHeader.text("Enter details for new CrowdFlower job");
  
  this._elmts.jobTabs.tabs();

  this._renderAllExistingJobs();
  this._renderAllColumns(this._elmts.columnList);
  
  var self = this;

  
  this._elmts.columnsPanel.hide();
  
  this._elmts.chkUploadToNewJob.click(function () {
	
	  if(self._elmts.chkUploadToNewJob.is(':checked')) {
		  self._elmts.columnsPanel.show();
	  }
	  else {
		  self._elmts.columnsPanel.hide();
	  }
	  
  });
  
  this._elmts.okButton.click(function() {
	  self._extension = {};
      self._extension.title= self._elmts.jobTitle.val();
      self._extension.instructions = self._elmts.jobInstructions.val();
      self._extension.content_type = "json";
      self._extension.column_names = [];
      
      self._extension.new_job = true; //TODO: check which tab is selected
      
      //TODO: check if cml exists, if not, create a default one from column names
      $('#columns input.zem-col:checked').each( function() {
    	  self._extension.column_names.push($(this).attr('value'));
      });
      
      
      console.log("Columns: " + self._extension.column_names);
      
      DialogSystem.dismissUntil(self._level - 1);
      self._onDone(self._extension);
  });
  
  
  this._elmts.cancelButton.click(function() {
	  
	  var curTabPanel = $('#jobTabs .ui-tabs-panel:not(.ui-tabs-hide)');
	  
	  var index = curTabPanel.index();
	  console.log("Index: " + index);
	  
    DialogSystem.dismissUntil(self._level - 1);
  });
   
  
  this._elmts.jobTitle.blur(function () {
	  var title = self._elmts.jobTitle.val();	  
	  if(title.length < 5 || title.length > 255  ) {
		  //TODO: add better visual clues
		  alert("Title should be between 5 and 255 chars.");
	  }
  });
  
  this._elmts.copyButton.click(function() {
	  var jobid = self._elmts.allJobsList.val();

	  if(jobid === "none") {
		  alert("First select job to copy!");
	  }
	  else {
		  self._copyAndUpdateJob(jobid);
	  }
	  
  });
  
  dismissBusy();
  this._level = DialogSystem.showDialog(this._dialog);
  
};

ZemantaCrowdFlowerDialog.prototype._copyAndUpdateJob = function(jobid) {
	
	var self = this;
	self._extension = {};
	self._extension.job_id = jobid;
	
	  ZemantaExtension.util.copyJob(self._extension, function(data){
		  console.log("Copy results: " + JSON.stringify(data));
		  self._updateJobList(data);
	  });
	
};


ZemantaCrowdFlowerDialog.prototype._updateJobList = function(data) {
	var selContainer = this._elmts.allJobsList;
	var jobs = data["jobs"];
	var selected = "";
	console.log("Data: " + JSON.stringify(data));
	
	selContainer.empty();
	
	$('<option name="opt_none" value="none">--- select a job --- </option>').appendTo(selContainer);
	
	for (var index = 0; index < jobs.length; index++) {
		var value = jobs[index];
		console.log("Value: " + value);
		
		if(value.id === data.job_id) {
			selected = " selected";
		} else {
			selected = "";
		}
		
		var job = $('<option name="opt_' + index + '" value=' + value.id + '' + selected + '>' + value.title + ' (job id: ' + value.id + ')</option>');		
		selContainer.append(job);

	}
};

ZemantaCrowdFlowerDialog.prototype._renderAllExistingJobs = function() {
	
	var self = this;
	var selContainer = self._elmts.allJobsList;
	var elemStatus = self._elmts.statusMessage;
	
	$('<option name="opt_none" value="none">--- select a job --- </option>').appendTo(selContainer);
	
	ZemantaExtension.util.loadAllExistingJobs(function(data, status) {
		
		elemStatus.html("Status: " + status);
		
		$.each(data, function(index, value) {
			
			var title = (value.title == null)? "Title not defined" : value.title;
			
			var job = $('<option name="opt_' + index + '" value=' + value.id + '>' + title + ' (job id: ' + value.id + ')</option>');
			
			selContainer.append(job);
		});
		
		selContainer.change(function() {
			//alert($(this).children(":selected").val());
			//get job data
			this._extension = {};
			this._extension.job_id = $(this).children(":selected").val();
			this._selectedJob = this._extension.job_id;
			
			console.log("Job id changed:" + JSON.stringify(this._extension));
			
			ZemantaExtension.util.getJobInfo(this._extension, function(data){
				  console.log("Updating job.");
					self._updateJobInfo(data);
			});

		});
		
	});
	
	
	
};

ZemantaCrowdFlowerDialog.prototype._updateJobInfo = function(data) {

	var self = this;
	var elm_jobTitle = self._elmts.extJobTitle;
	var elm_fields = self._elmts.extJobColumns;
	var elm_jobInstructions = self._elmts.extJobInstructions;

	console.log("Updating job..." + JSON.stringify(data));
	
	if(data["title"] === null ) {
		elm_jobTitle.val("(title undefined)");
	} else {
		elm_jobTitle.val(data["title"]);
	}
	
	if(data["instructions"] === null || data["instructions"] === "") {
		elm_jobInstructions.html("(instructions undefined)");
	}
	else {
		elm_jobInstructions.html(data["instructions"]);
	}
	
	self._elmts.extCmlFields.html(data["cml"]);
	
	$.each(data["fields"], function(index, value) {
		$('<input type="checkbox">' + value + '</input>').appendTo(elm_fields);
	});
	
	self._elmts.statusMessage.html(data["message"]);
	
};


ZemantaCrowdFlowerDialog.prototype._renderAllColumns = function() {
	  
	var self = this;
	var columns = theProject.columnModel.columns;
	
	var columnContainer = self._elmts.allColumns;
	var columnListContainer = self._elmts.columnList; //$('<div id="project-columns">');
	var chkid = 0;

	var renderColumns = function(columns, elem) {
		
		$.each(columns, function(index, value){
			var id = 'chk_' + chkid;
			var input = $('<input type="checkbox" class="zem-col" value="' + value.name + '" id="' + id + '">').appendTo(elem);
			$('<label for="' + id + '">' + value.name + '</label> <br/>').appendTo(elem);
			chkid++;
						
			//in case any other column is clicked, all-columns checked turns into false
			input.click(function() {
				$('input#all-cols').attr('checked',false);
			});
		});
		
	};
	
	var input = $('<input type="checkbox" value="all" id="all-cols">').appendTo(columnContainer);
	$('<label for="all-cols">All columns </label>').appendTo(columnContainer);
	$('<br /><br />').appendTo(columnContainer);
	renderColumns(columns, columnListContainer);
	
	//check all columns by default
	input.click(function() {
		$('#project-columns input.zem-col').each(function () {
			$(this).attr('checked', true);
		});
	});
	
};