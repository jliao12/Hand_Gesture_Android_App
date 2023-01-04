import os
from flask import Flask,  request

import fnmatch

UPLOAD_FOLDER = '/Users/junhuiliao/Desktop/upload'

app = Flask(__name__)
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

@app.route('/', methods=['POST'])
def upload_file():
    if request.method == 'POST':
        uploaded_file = request.files['file']
        count = 0
        filename = str(uploaded_file.filename)
        for file in os.listdir(UPLOAD_FOLDER):
            if fnmatch.fnmatch(file, filename+"*"):
                count = count + 1
        count = str(count + 1)
        filename = filename + "_PRACTICE_" + count +".mp4"
        uploaded_file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
    return "return"

if __name__ == "__main__":
    app.run(host="0.0.0.0")