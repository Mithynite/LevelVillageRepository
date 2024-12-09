import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {deletePost, fetchPostById, updatePost} from "../../api/postService.jsx";
import NavigationButton from "../../components/NavigationButton.jsx";
import {useNavigate} from "react-router-dom";

const PostDetails = ({isMyPost}) => {
    const navigate = useNavigate();
    const { id } = useParams(); // Extract the dynamic parameter from the URL
    const [post, setPost] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [editedTitle, setEditedTitle] = useState("");
    const [editedDescription, setEditedDescription] = useState("");

    const handlePostUpdate = async () => {
        setEditedTitle(editedTitle.trim());
        setEditedDescription(editedDescription.trim());

        if (editedTitle === "") setEditedTitle(post.title);
        if (editedTitle === "") setEditedDescription(post.description);

        console.log("Post: " + id + ", " + editedTitle + ", " + editedDescription)
        const updatedPost = {
            title: editedTitle,
            description: editedDescription,
        }
        await updatePost(id, updatedPost);
    };

    const handlePostDelete = async () => {
        await deletePost(id);
        navigate('/home');
    }

    const fetchPost = async (postId) => {

        try {
            const specificPost = await fetchPostById(postId);
            setPost(specificPost);
        }catch(error){
            console.error("Error while fetching the post: ", error);
            setError(error.message + "Something went wrong while fetching the post!");
        }finally {
            setLoading(false);
        }
        };
    useEffect(() => { // does its thing when the component loads
        fetchPost(id); //call the function
    }, [id]);

    if (loading) return <p>Loading...</p>;
    if (error) return <p>Error: {error}</p>;

    return (
        <div className="posts-container">
            <NavigationButton to="/home" label="Go Back to Home" />
            <div>{isMyPost ? (
                <div>
                    <input type="text" defaultValue={post.title} onChange={(e) => setEditedTitle(e.target.value)}/>
                    <br/>
                    <textarea defaultValue={post.description} onChange={(e) => setEditedDescription(e.target.value)}></textarea>
                    <button onClick={handlePostUpdate}>Update</button>
                    <button onClick={handlePostDelete}>Delete</button>
                </div>
            ) : (
                <div className="author-container">
                    <h1>{post.user.username}</h1>
                </div>
            )}
            </div>
            <div className="post-container">
                <p><strong>Published on:</strong> {new Date(post.created_at).toLocaleDateString()}</p>
            </div>
        </div>
    );
};
export default PostDetails;
