import React from 'react'

const Messages = ({ messages, currentUser, values }) => {

    let renderMessage = (message) => {
        const { sender, content, color } = message;
        const adjustedValues = values.map(value => value !== null ? value : 0);
        const messageFromMe = currentUser.username === message.sender;
        const className = messageFromMe ? "Messages-message currentUser" : "Messages-message";
        return (
            <li className={className}>
                <span
                    className="avatar"
                    style={{ backgroundColor: color }}
                />
                <div className="Message-content">
                    <div className="username">
                        {sender}
                    </div>
                    <div className="text">{content}</div><br/>
                    <div className="text">{"[" + adjustedValues.join(', ') + "]"}</div>
                </div>
            </li>
        );
    };

    return (
        <ul className="messages-list">
            {messages.map(msg => renderMessage(msg))}
        </ul>
    )
}


export default Messages