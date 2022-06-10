// See https://github.com/dialogflow/dialogflow-fulfillment-nodejs
// for Dialogflow fulfillment library docs, samples, and to report issues
'use strict';
 
const functions = require('firebase-functions');
const {WebhookClient} = require('dialogflow-fulfillment');
const {Card, Suggestion} = require('dialogflow-fulfillment');
 
process.env.DEBUG = 'dialogflow:debug'; // enables lib debugging statements
 
exports.dialogflowFirebaseFulfillment = functions.https.onRequest((request, response) => {
  const agent = new WebhookClient({ request, response });
  console.log('Dialogflow Request headers: ' + JSON.stringify(request.headers));
  console.log('Dialogflow Request body: ' + JSON.stringify(request.body));
 
  function welcome(agent) {
    agent.add(`Welcome to my agent!`);
  }
 
  function fallback(agent) {
    agent.add(`I didn't understand`);
    agent.add(`I'm sorry, can you try again?`);
  }

    
  function handleAppointment(agent) {
    let date;
    if(agent.parameters.AppointmentTime.date_time) {
      date = new Date(agent.parameters.AppointmentTime.date_time);
    } else {
    	date = new Date(agent.parameters.AppointmentTime);
    }
    if(date.getHours() >= 18 || date.getHours() < 8) {
    	agent.add(`I'm sorry, but there are no appointments at night, please specify another Appointment.`);
      	agent.followup_event={'name':'set_appointment_time'};
    } else {
   		agent.add(`You have booked an appointment at: ${date.toString()}`);
    }
  }
  
 const coronaSymptoms = ['cough', 'loss of taste', 'fever', 'tiredness', 'shortness of breath', 'chestpain'];
 function checkSymptoms(agent) {
   let coronaSymptomsCount = 0;
   for(let sym of agent.parameters.SymType) {
   		if(coronaSymptoms.includes(sym)) {
          	coronaSymptomsCount++;
        }
   }
   
   if(coronaSymptomsCount > 3) {
		response.json({
          payload: { hasMaybeCorona: true },
          fulfillmentText: "It seems, you might have caught the corona-virus. You should isolate imediately, and see a doctor as soon as possible. Would you like to book an appointment now?"
		});
   } else {
   		if(Number(agent.parameters.SymIntensity) <=2) {
    		agent.add("Your symptoms, don't seem to be very bad yet, we suggest you wait a little and contact us, if they get worse. Would you still like to see a doctor?");
   		} else if(Number(agent.parameters.SymIntensity) == 3) {
			agent.add("Your concern seems to be justified. Would you like to see a doctor?");
   		} else {
    		agent.add("We strongly suggest you, to see a doctor. Would you like to book an appointment now?");
   		}
   }

 }

  // // Uncomment and edit to make your own intent handler
  // // uncomment `intentMap.set('your intent name here', yourFunctionHandler);`
  // // below to get this function to be run when a Dialogflow intent is matched
  // function yourFunctionHandler(agent) {
  //   agent.add(`This message is from Dialogflow's Cloud Functions for Firebase editor!`);
  //   agent.add(new Card({
  //       title: `Title: this is a card title`,
  //       imageUrl: 'https://developers.google.com/actions/images/badges/XPM_BADGING_GoogleAssistant_VER.png',
  //       text: `This is the body text of a card.  You can even use line\n  breaks and emoji! 💁`,
  //       buttonText: 'This is a button',
  //       buttonUrl: 'https://assistant.google.com/'
  //     })
  //   );
  //   agent.add(new Suggestion(`Quick Reply`));
  //   agent.add(new Suggestion(`Suggestion`));
  //   agent.setContext({ name: 'weather', lifespan: 2, parameters: { city: 'Rome' }});
  // }

  // // Uncomment and edit to make your own Google Assistant intent handler
  // // uncomment `intentMap.set('your intent name here', googleAssistantHandler);`
  // // below to get this function to be run when a Dialogflow intent is matched
  // function googleAssistantHandler(agent) {
  //   let conv = agent.conv(); // Get Actions on Google library conv instance
  //   conv.ask('Hello from the Actions on Google client library!') // Use Actions on Google library
  //   agent.add(conv); // Add Actions on Google library responses to your agent's response
  // }
  // // See https://github.com/dialogflow/fulfillment-actions-library-nodejs
  // // for a complete Dialogflow fulfillment library Actions on Google client library v2 integration sample

  // Run the proper function handler based on the matched Dialogflow intent name
  let intentMap = new Map();
  intentMap.set('Default Welcome Intent', welcome);
  intentMap.set('Default Fallback Intent', fallback);
  intentMap.set('check_symptoms', checkSymptoms);
  intentMap.set('check_symptoms - yes - time', handleAppointment);
  // intentMap.set('your intent name here', yourFunctionHandler);
  // intentMap.set('your intent name here', googleAssistantHandler);
  agent.handleRequest(intentMap);
});
