let isWorking = false
let isGotResponse = false
let confirmMethod = 0;

try {

    (function (xhr) {

        let XHR = XMLHttpRequest.prototype;

        let open = XHR.open;
        let send = XHR.send;
        let setRequestHeader = XHR.setRequestHeader;
        let startTime


   //     let confirmMethod = 0;
        let delay = 1500;
        let delayMethod = false;
//        let isWorking = false;
//        let isGotResponse = false;
        //   var endtime

     console.log("inject from JS");



        window.addEventListener('message', function (event) {

            switch (event.data.type) {
                case "start":
                    // code block
                    isWorking = true
                    isGotResponse = false
                    //  console.log("start repeat")
                    clickRefreshButton()


                    break;
                case "stop":
                    // code block
                    isWorking = false
                    break;
                case "changeConfirmMethod":
                    // code block
                    // var res=confirmBooking222(tempGlobalInfo)
                    confirmMethod = event.data.text

                    break;

                case "changeDelay":
                    // code block
                    // var res=confirmBooking222(tempGlobalInfo)
                    delay = event.data.text

                    break;


                case "changeDelayMethod":
                    delayMethod = event.data.text

                    break;
                default:
                // code block
            }

        });


        XHR.open = function (method, url) {
            this._method = method;
            this._url = url;
            this._requestHeaders = {};
            // startTime = new Date()
            // this._startTime = startTime.toISOString();
            // console.log("!start time -" + this._startTime)

            return open.apply(this, arguments);
        };

        XHR.setRequestHeader = function (header, value) {
            this._requestHeaders[header] = value;
            return setRequestHeader.apply(this, arguments);
        };

        XHR.send = function (postData) {
   console.log("add eventlistener load");
            this.addEventListener('load', function () {

      console.log("start eventlistener load");
                var myUrl = this._url ? this._url.toLowerCase() : this._url;
                var urlForFetch = this._url

                //   console.log('myURL - ' + myUrl);
                //    console.log(myUrl.includes('exact_match'))
                if (myUrl.includes('exact_match')) {
                    //      console.log("post data - " + postData);
                    if (postData) {
                        if (typeof postData === 'string') {
                            try {
                                // here you get the REQUEST HEADERS, in JSON format, so you can also use JSON.parse
                                this._requestHeaders = postData;
                            } catch (err) {
                                console.log('Request Header JSON decode failed, transfer_encoding field could be base64');
                                console.log(err);
                            }
                        } else if (typeof postData === 'object' || typeof postData === 'array' || typeof postData === 'number' || typeof postData === 'boolean') {
                            // do something if you need
                        }
                    }

                    // here you get the RESPONSE HEADERS
                    //     console.log("this._requestHeaders - " + this._requestHeaders);
                    //if (this._requestHeaders. includes('EXACT_MATCH'))

                     console.log("isWorking before if=" + isWorking)
                    if (isWorking) {

                        var responseHeaders = this.getAllResponseHeaders();

                        if (this.responseType != 'blob' && this.responseText) {
                            // responseText is string or null
                            try {

                                // here you get RESPONSE TEXT (BODY), in JSON format, so you can use JSON.parse
                                var arr = this.responseText;
                                if (arr != null) {
                                    var objArr = JSON.parse(arr)
                                    let resultsCount = 0

                                    if (objArr) {
                                        let wo = objArr.workOpportunities

                                        if (wo) resultsCount = Object.keys(wo).length
                                    }
  console.log("isGotResponse =" + isGotResponse)

                                    isGotResponse = true

  console.log("resultsCount =" + resultsCount)
                                    if (resultsCount > 0) {
                                        isWorking = false
                                        window.postMessage({
                                            type: 'found',
                                            text: objArr.workOpportunities[0].id
                                        },
                                            '*' /* targetOrigin: any */);

                                        if (confirmMethod == 1) {
                                            confirmBooking1(objArr)
                                        } else
                                            if (confirmMethod == 2) {
                                                confirmBooking2(objArr)
                                            }


                                    }

                                    else clickRefreshButtonhWithDelay(delay,delayMethod,isWorking)

                                } else clickRefreshButtonhWithDelay(delay,delayMethod,isWorking)



                            } catch (err) {
                                clickRefreshButtonhWithDelay(delay,delayMethod,isWorking)

                            }
                        }

                    }

                }
            });

            return send.apply(this, arguments);
        };



    })(XMLHttpRequest);
}
catch {
    //  alert("Main error 2 - " + err)
}



function confirmBooking1(mainInfo) {
    const wo = mainInfo.workOpportunities[0]

    const foundLoad = document.getElementById(wo.id)

    const buttonBook = foundLoad.querySelector('.btn')

    buttonBook.click()

}


function confirmBooking2(mainInfo) {

    const wo = mainInfo.workOpportunities[0]

    const foundLoad = document.getElementById(wo.id)


    const buttonBook = foundLoad.querySelector('.btn')

    buttonBook.click()

    waitForConfirm(foundLoad)


}



function waitForConfirm(foundLoad) {
    //  console.log("wait for confirm")
    selector = ".confirmation-body__footer__confirm-button"
    return new Promise(function (res, rej) {
        waitForElementToDisplay(selector, 0);

        function waitForElementToDisplay(selector, time) {
            const confirm = foundLoad.querySelector(selector)
            if (confirm != null) {

              //  confirm.click()
                res(confirm);

                //console.log("Confirm button found! Confirm!" + confirm)

            }
            else {
                setTimeout(function () {
                    waitForElementToDisplay(selector, time);
                }, time);
            }
        }
    });
}

function waitForResponse() {
    console.log("isGotResponse=" + isGotResponse)
    return new Promise(function (res, rej) {
        console.log("isGotResponse2=" + isGotResponse)
        waitForGetResponse();

        function waitForGetResponse() {

            if (isGotResponse) {
         //       console.log("isGotResponse3=" + isGotResponse)
                //      confirm.click()
                res(isGotResponse);

            //    console.log("got response ")

            }
            else {
                //  console.log("isGotResponse4="+isGotResponse)
                setTimeout(function () {
                    //    console.log("isGotResponse5="+isGotResponse)
                    waitForGetResponse();
                }, 0);
            }
        }
    });
}

function clickRefreshButton() {
    const refreshBtn = document.querySelector('.loadboard-reload__refresh-icon--reload-icon');

    refreshBtn.click()

   console.log("refresh clicked")

    return
};



function repeat(ms) {
    let i = 0;

    function f() {

        clickRefreshButton()
        i += 1;



        if (isWorking) {
            setTimeout(function () {


                if (isWorking) {

                    f();
                }

            }, ms);
        }
    }

    if (isWorking) {

        f();
    }
}


function clickRefreshButtonhWithDelay(delayMs, delayMethodIn,isWorking) {

    if (delayMethodIn == true) {
        delayMs = Math.floor(Math.random() * delayMs * .3) + delayMs * .7;
    }


    if (isWorking) {
        setTimeout(function () {


            if (isWorking) {

                clickRefreshButton()
            }

        }, delayMs);
    }
}
