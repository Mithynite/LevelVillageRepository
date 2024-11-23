import React, { useState, useEffect } from 'react';
import { getPosts } from '../api/postService'; // Import your API service
import '../styles/common-style.css'; // Adjust the path to your CSS file

const HomePage = () => {
    const [posts, setPosts] = useState([]); // State to store posts
    const [error, setError] = useState(null); // State to store errors

    // Fetch posts when the component mounts
    useEffect(() => {
        const fetchPosts = async () => {
            try {
                const fetchedPosts = await getPosts(); // Call the API
                setPosts(fetchedPosts); // Update state with the posts
            } catch (err) {
                console.error('Failed to fetch posts:', err);
                setError('Failed to fetch posts. Please try again later.');
            }
        };

        fetchPosts();
    }, []); // Empty dependency array ensures it only runs once on mount

    return (
        <div className="dashboard">
            <h1>Welcome to the HomePage!</h1>

            {/* Show an error message if the API call fails */}
            {error && <p className="error-message">{error}</p>}

            {/* Display posts */}
            <div className="posts-container">
                {posts.length > 0 ? (
                    posts.map((post) => (
                        <div key={post.id} className="post-card">
                            <h2>{post.title}</h2>
                            <p>{post.description}</p>
                            <small>Created at: {new Date(post.timestamp).toLocaleString()}</small>
                        </div>
                    ))
                ) : (
                    <p>No posts available</p>
                )}
            </div>
        </div>
    );
};

export default HomePage;
