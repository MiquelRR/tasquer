# Welcome to Cloud Functions for Firebase for Python!
# To get started, simply uncomment the below code or create your own.
# Deploy with `firebase deploy`

import json
from logging import Logger
from os.path import isdir

import google.cloud.firestore
from firebase_functions import https_fn
from firebase_admin import initialize_app, firestore, credentials


cred = credentials.Certificate("???????????????????????.json")
options={
    "projectId":"???????????????????",
    "serviceAccountId":"??????????????",
    "storageBucket":"?????????????.firebasestorage.app",
    "databaseURL":"projects/?????????????/databases/(default)",
    "httpTimeout": "360"
}

initialize_app(cred,options=options)

db: google.cloud.firestore.Client = firestore.client()
auth_sources_doc = db.collection("settings").document("auth_sources").get()
auth_sources = auth_sources_doc.to_dict().get("sources", []) if auth_sources_doc.exists else []


@https_fn.on_request(timeout_sec=10)
def on_request_hello(req: https_fn.Request) -> https_fn.Response:
    sender = req.args.get("sender")
    if sender in auth_sources:
        return https_fn.Response(f"Hello, {sender}! look: {auth_sources}", status=200)
    else:
        return https_fn.Response("Something happens", status=404)


@https_fn.on_request(timeout_sec=10)
def task_server(req: https_fn.Request) -> https_fn.Response:
    sender = req.args.get("sender")
    tasks = dict()
    if sender in auth_sources:
        image_docs = db.collection("tasks").get()
        for doc in image_docs:
            task_data = doc.to_dict()
            task_data["id"] = doc.id
            if "date" in task_data:
                try:
                    task_data["date"]=task_data["date"].date().isoformat()
                except:
                    task_data["date"]=None
            else:
                task_data["date"]=None
            tasks.update({doc.id : task_data})
        return https_fn.Response(
            json.dumps(tasks), status=200
        )
    else:
        return https_fn.Response("Something happens", status=404)


