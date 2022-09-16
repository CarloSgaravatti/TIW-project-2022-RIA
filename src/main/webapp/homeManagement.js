/**
 * 
 */
 
{
	var pageOrchestrator, directoryTree, subdirectoryView, dragAndDropManager, contentModifier, 
		formsManager, modalDisplayer, messageDisplayer;
	pageOrchestrator = new PageOrchestrator();
	
	window.addEventListener("load", () => {
	    if (sessionStorage.getItem("username") == null) {
	      window.location.href = "index.html";
	    } else {
	      pageOrchestrator.start();
	      pageOrchestrator.refresh();
	    }
    });
	
	function DirectoryTree(_directoryUl, _recycleBin, _createDirectoryButton) {
		this.directoryUl = _directoryUl;
		this.recycleBin = _recycleBin;
		this.createDirectoryButton = _createDirectoryButton;
		
		this.update = function() {
			var self = this;
			makeCall("GET", "GetDirectoryTree", null, 
				function(response) {
					if (response.readyState == XMLHttpRequest.DONE) {
						let message = response.responseText;
						messageDisplayer.reset();
						if (response.status == 200) {
							let directories = JSON.parse(message);
							self.reset();
							self.showDirectoryTree(directories);
							if (directories.length == 0) {
								messageDisplayer.displayPersonalMessage("You have no directories, create one directory first.")
							} else pageOrchestrator.onDirectoryTreeRefresh();
						} else if (response.status == 500){
							messageDisplayer.displayMessage(message);
						}
					}
				});
		}
		
		this.reset = function() {
			this.directoryUl.innerHTML = "";
		}
		
		this.showDirectoryTree = function(directories) {
			var self = this;
			var li, ul, text, button;
			li = document.createElement("li");
			text = document.createTextNode("Recycle bin");
			li.className = "directoryLi";
			li.appendChild(text);
			li.id = "recycleBin";
			this.recycleBin = li;
			this.directoryUl.appendChild(li);
			dragAndDropManager.registerRecycleBinEvents(li);
			directories.forEach(function(directory) {
				li = document.createElement("li");
				li.setAttribute("directoryId", directory.directoryId);
				text = document.createTextNode(directory.name);
				li.className = "directoryLi";
				li.appendChild(text);
				button = document.createElement("button");
				button.className = "createContentButton";
				button.appendChild(document.createTextNode("Add Subdirectory"));
				li.appendChild(button);
				dragAndDropManager.registerDirectoryEvents(li);
				self.directoryUl.appendChild(li);
				var subdirectories = directory.subdirectories;
				var subdirectoryLi, subdirectoryText;
				var subdirectoryUl = (function(subdirectoryList) {
					ul = document.createElement("ul");
					ul.className = "subdirectoryUl";
					subdirectoryList.forEach(function(subdirectory) {
						subdirectoryLi = document.createElement("li");
						subdirectoryLi.setAttribute("subdirectoryId", subdirectory.directoryId);
						subdirectoryText = document.createTextNode(subdirectory.name);
						var anchor = document.createElement("a");
						anchor.className = "subdirectoryAnchor";
						anchor.href = "#";
						anchor.appendChild(subdirectoryText);
						anchor.addEventListener("click", () => {
							pageOrchestrator.refreshOnlyDocumentsPart(subdirectory.directoryId, false, false);
						});
						anchor.id = "subdirectoryId" + subdirectory.directoryId;
						subdirectoryLi.className = "subdirectoryLi";
						subdirectoryLi.appendChild(anchor);
						var addDocbutton = document.createElement("button");
						addDocbutton.className = "createContentButton";
						addDocbutton.appendChild(document.createTextNode("Add Document"));						
						subdirectoryLi.appendChild(addDocbutton);
						formsManager.registerAddDocumentButtonEvents(addDocbutton);
						dragAndDropManager.registerSubdirectoryEvents(subdirectoryLi);
						ul.appendChild(subdirectoryLi);	
					});
					return ul;
				})(subdirectories);
				formsManager.registerSubdirectoryForm(button, subdirectoryUl, directory.directoryId);
				self.directoryUl.appendChild(subdirectoryUl);
			});
		}
	}
	
	function SubdirectoryView(_documentsDiv) {
		this.documentsDiv = _documentsDiv;
		var currentSubdirectoryId = 0;
		
		this.getCurrentSubdirectoryId = function() {
			return currentSubdirectoryId;
		}
		
		this.update = function(subdirectoryId, subdirectoryName) {
			var self = this;
			makeCall("GET", "GetSubdirectory?subdirectoryId=" + subdirectoryId, null, 
				function(response) {
					if (response.readyState == XMLHttpRequest.DONE) {
						var message = response.responseText;
						messageDisplayer.reset();
						if (response.status == 200) {
							var docs = JSON.parse(message);
							self.reset();
							currentSubdirectoryId = subdirectoryId;
							self.showDocumentsOfSubdirectory(docs, subdirectoryName);
						} else {
							messageDisplayer.displayMessage(message);
						}
					}
				});
		}
		
		this.reset = function() {
			this.documentsDiv.innerHTML = "";
			currentSubdirectoryId = 0;
		}
		
		this.showDocumentsOfSubdirectory = function(docs, subdirectoryName) {
			var button, self = this;
			var p = document.createElement("p");
			if (docs.length == 0) {
				p.appendChild(document.createTextNode(subdirectoryName + " is an empty subdirectory, add a document first."));
				this.documentsDiv.appendChild(p);
				return;
			}
			p.appendChild(document.createTextNode("You are viewing documents of subdirectory: " + subdirectoryName));
			this.documentsDiv.appendChild(p);
			docs.forEach(function(doc) {
				button = document.createElement("button");
				button.className = "documentAccordion";
				button.setAttribute("documentId", doc.documentId);
				button.appendChild(document.createTextNode(doc.name + doc.documentType));
				dragAndDropManager.registerDocumentEvents(button);
				button.addEventListener("click", (e) => {
					self.changeShowDocument(e.target);
				});
				divElement = document.createElement("div");
				divElement.className = "documentPanel";
				divElement.id = "document" + doc.documentId;
				var p1 = document.createElement("p");
				p1.appendChild(document.createTextNode("Creation date: " + doc.creationDate));
				var p2 = document.createElement("p");
				p2.appendChild(document.createTextNode("Summary: " + doc.summary));
				var p3 = document.createElement("p");
				p3.appendChild(document.createTextNode("Type: " + doc.documentType));
				divElement.appendChild(p1);
				divElement.appendChild(p2);
				divElement.appendChild(p3);
				self.documentsDiv.appendChild(button);
				self.documentsDiv.appendChild(divElement);
			});
		}
		
		this.changeShowDocument = function(documentButton) {
			var documentId = documentButton.getAttribute("documentId");
			var documentDiv = document.getElementById("document" + documentId);
			if (documentButton.className == "activeDocumentAccordion") {
				documentButton.className = "documentAccordion";
			} else {
				documentButton.className = "activeDocumentAccordion";
			}
			if (documentDiv.className == "visibleDocumentPanel") {
				documentDiv.className = "documentPanel";
			} else {
				documentDiv.className = "visibleDocumentPanel";
			}
		}
	}
	
	function DragAndDropManager() {
		var draggedButton;
		var draggedDirectoryId;
		var draggedDirectoryName;
		
		this.registerDocumentEvents = function(docButton) {
			docButton.draggable = true;
			docButton.addEventListener("dragstart", this.onDragStart);
		} 
		
		this.registerSubdirectoryEvents = function(subdirectoryLi) {
			subdirectoryLi.firstChild.draggable = true;
			subdirectoryLi.firstChild.addEventListener("dragstart", this.onSubdirectoryDragStart);
			subdirectoryLi.addEventListener("dragover", this.onDragOver);
			subdirectoryLi.addEventListener("dragleave", this.onDragLeave);
			subdirectoryLi.addEventListener("drop", this.onDrop);
		};
		
		this.registerDirectoryEvents = function(directoryLi) {
			directoryLi.draggable = true;
			directoryLi.addEventListener("dragstart", this.onDirectoryDragStart);
		};
		
		this.registerRecycleBinEvents = function(recycleBin) {
			recycleBin.addEventListener("dragover", this.onDragOverOnBin);
			recycleBin.addEventListener("dragleave", this.onDragLeaveOnBin);
			recycleBin.addEventListener("drop", this.onDropOnBin);
		};
		
		this.onDragStart = function(event) {
			draggedButton = event.target.closest("button");
		};
		
		this.onSubdirectoryDragStart = function(event) {
			var subdirectoryLi = event.target.closest("li");
			draggedDirectoryId = subdirectoryLi.getAttribute("subdirectoryId");
			draggedDirectoryName = subdirectoryLi.firstChild.textContent;
		};
		
		this.onDirectoryDragStart = function(event) {
			var directoryLi = event.target.closest("li");
			draggedDirectoryId = directoryLi.getAttribute("directoryId");
			draggedDirectoryName = directoryLi.firstChild.textContent;
		};
		
		this.onDragOver = function(event) {
			if (draggedButton != undefined) {
				event.preventDefault(); //to make the drop event fired
				var subdirectoryAnchor = event.target.closest("li").firstChild;
				subdirectoryAnchor.className = "selectedAnchor";
			}
		};
		
		this.onDragLeave = function(event) {
			if (draggedButton != undefined) {
				var subdirectoryAnchor = event.target.closest("li").firstChild;
				subdirectoryAnchor.className = "subdirectoryAnchor";
			}
		};
		
		this.onDrop = function(event) {
			if (draggedButton != undefined) {
				var subdirectoryLi = event.target.closest("li");
				subdirectoryLi.firstChild.className = "subdirectoryAnchor";
				var subdirectoryDestId = subdirectoryLi.getAttribute("subdirectoryId");
				var documentId = draggedButton.getAttribute("documentId");
				contentModifier.moveDocument(documentId, subdirectoryDestId);
				draggedButton = undefined;
			}
		};
		
		this.onDragOverOnBin = function(event) {
			event.preventDefault();
			event.target.closest("li").className = "selectedRecycleBin";
		};
		
		this.onDragLeaveOnBin = function(event) {
			event.target.closest("li").className = "directoryLi";
		};
		
		this.onDropOnBin = function(event) {
			event.target.closest("li").className = "directoryLi";
			if (draggedButton != undefined) {
				var documentId = draggedButton.getAttribute("documentId");
				var documentName = draggedButton.textContent;
				modalDisplayer.showDocumentDeleteModal(documentId, documentName);
				draggedButton = undefined;
			} else if (draggedDirectoryId != undefined) {
				modalDisplayer.showDirectoryDeleteModal(draggedDirectoryId, draggedDirectoryName);
				draggedDirectoryId = undefined;
			}
		};
	}
	
	function ModalDisplayer(_modalContainer, _modalWindow, _confirmDeleteButton, _abortDeleteButton, _modalP, _modalCloseButton) {
		this.modalContainer = _modalContainer;
		this.modalWindow = _modalWindow;
		this.confirmDeleteButton = _confirmDeleteButton;
		this.abortDeleteButton = _abortDeleteButton;
		this.modalP = _modalP;
		this.modalCloseButton = _modalCloseButton;
		
		var lastDocumentId, lastDirectoryId, isDeletingDocument = false;
		
		this.showDocumentDeleteModal = function(documentId, documentName) {
			isDeletingDocument = true;
			lastDocumentId = documentId;
			this.modalP.innerHTML = "";
			this.modalP.appendChild(document.createTextNode("You are deleting document " +
				documentName + ". Do you want to continue?"	
			));
			this.modalContainer.style.display = "block";
		};
		
		this.showDirectoryDeleteModal = function(directoryId, directoryName) {
			isDeletingDocument = false;
			lastDirectoryId = directoryId;
			this.modalP.innerHTML = "";
			this.modalP.appendChild(document.createTextNode("You are deleting " +
				directoryName + ". Do you want to continue?"	
			));
			this.modalContainer.style.display = "block";
		};
		
		this.registerEvents = () => {
			this.confirmDeleteButton.addEventListener("click", this.confirmDelete);
			this.abortDeleteButton.addEventListener("click", this.abortDelete);
			this.modalCloseButton.addEventListener("click", this.abortDelete);
		}
		
		this.confirmDelete = () => {
			modalDisplayer.modalContainer.style.display = "none";
			if (isDeletingDocument) contentModifier.deleteDocument(lastDocumentId);
			else contentModifier.deleteDirectory(lastDirectoryId);
		}
		
		this.abortDelete = () => {
			modalDisplayer.modalContainer.style.display = "none";
		}
	}
	
	function ContentModifier() {
		
		this.createDocument = function(form) {
			if (form.checkValidity()) {
				makeCall("POST", "CreateDocument", form, 
					function(request) {
						if (request.readyState == XMLHttpRequest.DONE) {
							var message = request.responseText; //contains subdirectoryId (if 200)  
							if (request.status == 200) {
								pageOrchestrator.refreshOnlyDocumentsPart(parseInt(message, 10), false, true);
							} else {
								messageDisplayer.reset();
								messageDisplayer.displayMessage(message);
							}
						}
					});
			}
		}
		
		this.createSubdirectory = function(form) {
			if (form.checkValidity()) {
				makeCall("POST", "CreateSubdirectory", form, 
					function(request) {
						if (request.readyState == XMLHttpRequest.DONE) {
							if (request.status == 200) {
								pageOrchestrator.refresh();
							} else {
								var message = request.responseText;
								messageDisplayer.reset();
								messageDisplayer.displayMessage(message);
							}
						}
					});
			}
		}
		
		this.createDirectory = function(form) {
			if (form.checkValidity()) {
				makeCall("POST", "CreateDirectory", form, 
					function(request) {
						if (request.readyState == XMLHttpRequest.DONE) {
							if (request.status == 200) {
								pageOrchestrator.refresh();
							} else {
								var message = request.responseText;
								messageDisplayer.reset();
								messageDisplayer.displayMessage(message);
							}
						}
					});
			}
		}
		
		this.moveDocument = function(documentId, subdirectoryId) {
			makeCall("POST", "MoveDocument?documentId=" + documentId + "&subdirectoryId=" + subdirectoryId, null,
				function(request) {
					if (request.readyState == XMLHttpRequest.DONE) {
						var message = request.responseText;
						if (request.status == 200) {
							var subdirectory = JSON.parse(message);
							pageOrchestrator.refreshOnlyDocumentsPart(subdirectory.directoryId, false, false);
						} else {
							messageDisplayer.reset();
							messageDisplayer.displayMessage(message);
						}
					}
				});
		}
		
		this.deleteDirectory = function(directoryId) {
			makeCall("POST", "DeleteDirectory?directoryId=" + directoryId, null, 
				function(request) {
					if (request.readyState == XMLHttpRequest.DONE) {
						var message = request.responseText;
						if (request.status == 200) {
							pageOrchestrator.refresh();
						} else {
							messageDisplayer.reset();
							messageDisplayer.displayMessage(message);
						}
					}
				});
		}
		
		this.deleteDocument = function(documentId) {
			makeCall("POST", "DeleteDocument?documentId=" + documentId, null, 
				function(request) {
					if (request.readyState == XMLHttpRequest.DONE) {
						var message = request.responseText;
						if (request.status == 200) {
							var subdirectory = JSON.parse(message);
							pageOrchestrator.refreshOnlyDocumentsPart(subdirectory.directoryId, false, true);
						} else {
							messageDisplayer.reset();
							messageDisplayer.displayMessage(message);
						}
					}
				});
		}
	}
	
	function FormsManager(_createDirectoryButton, _createDocumentForm) {
		this.createDirectoryButton = _createDirectoryButton;
		this.createDocumentForm = _createDocumentForm;
		
		this.createDirectoryForm = (function() {
			var form = document.createElement("form");
			form.method = "post";
			form.action = "#";
			form.className = "createDirectoryForm";
			var inputName = document.createElement("input");
			inputName.type = "text";
			inputName.name = "name";
			inputName.required = true;
			form.appendChild(inputName);
			var createButton = document.createElement("input");
			createButton.id = "directoryButton";
			createButton.type = "button";
			createButton.value = "Create Directory";
			form.appendChild(createButton);
			var abortButton = document.createElement("input");
			abortButton.id = "abortDirectoryButton"; 
			abortButton.type = "button";
			abortButton.value = "Cancel";
			form.appendChild(abortButton);
			return form;
		})();
		
		var lastAddDocumentButtonClicked;
		
		this.registerEvents = function(directoryUl) {
			//directory form
			var directoryLi = document.createElement("li");
				directoryLi.className = "directoryLi";
			this.createDirectoryButton.addEventListener("click", () => {
				this.createDirectoryButton.className = "hiddenElement";
				directoryLi.appendChild(this.createDirectoryForm);
				if (directoryUl.children.lenght == 1) directoryUl.appendChild(directoryLi);
				else directoryUl.insertBefore(directoryLi, directoryUl.children[1]);
				this.createDirectoryForm.className = "createDirectoryForm";
			});
			var formButton = this.createDirectoryForm.children[1];
			var abortButton = this.createDirectoryForm.children[2];
			formButton.addEventListener("click", (e) => {
				contentModifier.createDirectory(e.target.closest("form"));
				this.createDirectoryButton.className = "createContentButton";
				directoryUl.removeChild(directoryLi);
			});	
			abortButton.addEventListener("click", () => {
				this.createDirectoryButton.className = "createContentButton";
				directoryUl.removeChild(directoryLi);
			});
			//document form
			var confirmButton = document.getElementById("createDocumentButton");
			var cancelButton = document.getElementById("cancelDocumentCreation");
			confirmButton.addEventListener("click", (e) => {
				contentModifier.createDocument(e.target.closest("form"));
				this.createDocumentForm.className = "hiddenElement";
				lastAddDocumentButtonClicked.className = "createContentButton";
			});
			cancelButton.addEventListener("click", () => {
				this.createDocumentForm.className = "hiddenElement";
				lastAddDocumentButtonClicked.className = "createContentButton";
			});
		};
		
		this.reset = function() {
			this.createDirectoryButton.className = "createContentButton";
			this.createDirectoryForm.className = "hiddenElement";
			this.createDocumentForm.className = "hiddenElement";
			if (lastAddDocumentButtonClicked) lastAddDocumentButtonClicked.className = "createContentButton";
		};
		
		this.registerSubdirectoryFormEvents = function(displayerButton, formToDisplay, createButton, abortButton) {
			displayerButton.addEventListener("click", () => {
				formToDisplay.className = "";
				displayerButton.className = "hiddenElement";
			});
			createButton.addEventListener("click", (e) => {
				contentModifier.createSubdirectory(e.target.closest("form"));
			});
			abortButton.addEventListener("click", (e) => {
				displayerButton.className = "createContentButton";
				e.target.closest("form").className = "hiddenElement";
			});
		};
		
		this.registerAddDocumentButtonEvents = function(addDocButton) {
			var subdirectoryLi = addDocButton.closest("li");
			var subdirectoryId = subdirectoryLi.getAttribute("subdirectoryId");
			addDocButton.addEventListener("click", () => {
				if (lastAddDocumentButtonClicked) {
					lastAddDocumentButtonClicked.className = "createContentButton";
				}
				if (subdirectoryId != subdirectoryView.getCurrentSubdirectoryId()) {
					pageOrchestrator.refreshOnlyDocumentsPart(subdirectoryId);
				}
				this.createDocumentForm.className = "";
				var hiddenInput = document.getElementById("subdirectoryIdForDocument");
				hiddenInput.value = subdirectoryId;
				addDocButton.className = "hiddenElement";
				lastAddDocumentButtonClicked = addDocButton;
			});
		}
		
		this.registerSubdirectoryForm = function(addSubdirButton, subdirectoryUl, directoryId) {
			addSubdirButton.addEventListener("click", () => {
				addSubdirButton.className = "hiddenElement";
				var form = document.createElement("form");
				form.method = "post";
				form.action = "#";
				form.className = "createDirectoryForm";
				var inputName = document.createElement("input");
				inputName.type = "text";
				inputName.name = "name";
				inputName.required = true;
				form.appendChild(inputName);
				var hiddenInput = document.createElement("input");
				hiddenInput.type = "hidden";
				hiddenInput.name = "directoryId";
				hiddenInput.value = directoryId;
				form.appendChild(hiddenInput);
				var createButton = document.createElement("input");
				createButton.type = "button";
				createButton.value = "Create Subdirectory";
				form.appendChild(createButton);
				var abortButton = document.createElement("input");
				abortButton.type = "button";
				abortButton.value = "Cancel";
				form.appendChild(abortButton);
				var subdirectoryLi = document.createElement("li");
				subdirectoryLi.className = "subdirectoryLi";
				subdirectoryLi.appendChild(form);
				subdirectoryUl.insertBefore(subdirectoryLi, subdirectoryUl.firstChild);
				createButton.addEventListener("click", () => {
					addSubdirButton.className = "createContentButton";
					contentModifier.createSubdirectory(form);
					subdirectoryUl.removeChild(subdirectoryLi);	
				});
				abortButton.addEventListener("click", () => {
					addSubdirButton.className = "createContentButton";
					subdirectoryUl.removeChild(subdirectoryLi);
				});
			});
		}
	}
	
	function MessageDisplayer(_messageP, _personalMessageP) {
		this.messageP = _messageP;
		this.personalMessageP = _personalMessageP;
		var isShowingMessage = false;
		
		this.displayMessage = function(message) {
			this.personalMessageP.textContent = "";
			var messageTextNode = document.createTextNode(message);
			this.messageP.appendChild(messageTextNode);
			isShowingMessage = true;
		}
		
		this.displayPersonalMessage = function(message) {
			this.messageP.textContent = "";
			var messageTextNode = document.createTextNode(message);
			this.personalMessageP.appendChild(messageTextNode);
			isShowingMessage = true;
		}
		
		this.reset = function() {
			this.personalMessageP.textContent = "";
			this.messageP.textContent = "";
			isShowingMessage = false;
		}
		
		this.isShowingMessage = function() {
			return isShowingMessage;
		}
	}
	
	function PageOrchestrator() {
		const subdirectoryViewStory = [];
		
		this.start = () => {
			var directoryUl = document.getElementById("directoryTree");
			directoryTree = new DirectoryTree(directoryUl);
			subdirectoryView = new SubdirectoryView(
				document.getElementById("documentsOfSubdirectory"), 
				document.getElementById("recycleBin")
			);
			dragAndDropManager = new DragAndDropManager();
			document.getElementById("back").addEventListener("click", this.back);
			contentModifier = new ContentModifier();
			formsManager = new FormsManager(
				document.getElementById("createDirectoryButton"),
				document.getElementById("createDocumentForm")
			);
			formsManager.registerEvents(directoryUl);
			modalDisplayer = new ModalDisplayer(
				document.getElementById("modalContainer"),
				document.getElementById("modal"),
				document.getElementById("confirmDelete"),
				document.getElementById("abortDelete"),
				document.getElementById("modalContent"),
				document.getElementById("closeModalXButton")
			);
			modalDisplayer.registerEvents();
			messageDisplayer = new MessageDisplayer(
				document.getElementById("message"),
				document.getElementById("personalMessage")
			);
				
		};
		
		this.refresh = () => {
			subdirectoryView.reset();
			directoryTree.reset();
			formsManager.reset();
			messageDisplayer.reset();
			directoryTree.update();
		};
		
		this.onDirectoryTreeRefresh = () => {
			if (subdirectoryViewStory.length != 0) {
				var lastSubdirectoryId = subdirectoryViewStory[subdirectoryViewStory.length - 1];
				if (document.getElementById("subdirectoryId" + lastSubdirectoryId)) {
					this.refreshOnlyDocumentsPart(lastSubdirectoryId, true, false);
				} else this.back();
			} else {
				var subdirectories = document.getElementsByClassName("subdirectoryLi");
				var firstSubdirectoryId = subdirectories[0].getAttribute("subdirectoryId");
				this.refreshOnlyDocumentsPart(firstSubdirectoryId, false, false);
			}
		}
		
		this.refreshOnlyDocumentsPart = (subdirectoryId, fromBack, fromModifyDocument) => {
			var subdirectoryAnchor = document.getElementById("subdirectoryId" + subdirectoryId);
			subdirectoryView.update(subdirectoryId, subdirectoryAnchor.textContent);
			if (!fromBack && !fromModifyDocument) subdirectoryViewStory.push(subdirectoryId);
			messageDisplayer.reset();
			formsManager.reset();
		};
		
		this.back = () => {
			if (messageDisplayer.isShowingMessage()) {
				messageDisplayer.reset();
			} else if (subdirectoryViewStory.length == 0) {
				subdirectoryView.reset();
			} else {
				var lastSubdirectoryId = subdirectoryViewStory[subdirectoryViewStory.length - 1];
				var subdirectoryAnchor = document.getElementById("subdirectoryId" + lastSubdirectoryId);
				if (subdirectoryAnchor) {
					var subdirectoryLi = subdirectoryAnchor.parentNode;
					subdirectoryLi.children[1].className = "createContentButton";
				}
				formsManager.reset();
				do {
					subdirectoryViewStory.pop();
				} while(subdirectoryViewStory.length != 0 && !document.getElementById("subdirectoryId" + subdirectoryViewStory[subdirectoryViewStory.length - 1]));
				if (subdirectoryViewStory.length > 0) {
					var subdirectoryId = subdirectoryViewStory[subdirectoryViewStory.length - 1];
					this.refreshOnlyDocumentsPart(subdirectoryId, true, false);
				} else {
					this.onDirectoryTreeRefresh();
				}
			}
		};
	}	
}