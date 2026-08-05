// App.js
import React from 'react';
import AudioPlayer from './AudioPlayer';
import API_URL from '../Config';

const App = () => {
    const audioSrc = `${API_URL}/api/v2/1725357758523_file_example_MP3_1MG.mp3/file`; // Replace with your audio file path

    return (
        <div>
            <h1>Audio Player</h1>
            <AudioPlayer audioSrc={audioSrc} />
        </div>
    );
};

export default App;
