import PropTypes from "prop-types";

const PostCard = ({ post, likedPostIds, handlePostClick, handlePostLike }) => {
    const isLiked = likedPostIds.has(post.id);
    const truncateText = (text, maxLength = 200) => {
        if (!text) return "";
        return text.length > maxLength ? text.substring(0, maxLength) + "..." : text;
    };

    return (
        <div
            key={post.id}
            className="post-card"
            onClick={() => handlePostClick(post.id)}
        >
            {/* Like Button */}
            <label
                className="container"
                onClick={(e) => e.stopPropagation()}
            >
                <input
                    type="checkbox"
                    checked={isLiked}
                    onChange={() => handlePostLike(post.id)}
                />
                <div className="checkmark">
                    <svg viewBox="0 0 256 256">
                        <rect fill="none" height="256" width="256"></rect>
                        <path
                            d="M224.6,51.9a59.5,59.5,0,0,0-43-19.9,60.5,60.5,0,0,0-44,17.6L128,59.1l-7.5-7.4C97.2,
                            28.3,59.2,26.3,35.9,47.4a59.9,59.9,0,0,0-2.3,87l83.1,83.1a15.9,15.9,0,0,0,22.6,
                            0l81-81C243.7,113.2,245.6,75.2,224.6,51.9Z"
                            strokeWidth="20px"
                            stroke="#FFF"
                            fill="none"
                        ></path>
                    </svg>
                </div>
            </label>

            {/* Post Info */}
            <p>{post.username}</p>
            <h2>{post.title}</h2>
            <p className="post-description">{truncateText(post.description)}</p>

            {/* Skills */}
            {post.skills && post.skills.length > 0 && (
                <div className="post-skills">
                    {post.skills.map(skill => (
                        <span key={skill.id} className="skill-tag">
                    {skill.skillName}
                        </span>
                    ))}
                </div>
            )}

            <small>
                {(() => {
                    const date = new Date(post.createdAt);
                    return `${date.getDate()}. ${date.getMonth() + 1}. ${date.getFullYear()}`;
                })()}
            </small>
        </div>
    );
};

PostCard.propTypes = {
    post: PropTypes.shape({
        id: PropTypes.number.isRequired,
        username: PropTypes.string.isRequired,
        title: PropTypes.string.isRequired,
        description: PropTypes.string.isRequired,
        createdAt: PropTypes.string.isRequired,
        skills: PropTypes.arrayOf(
            PropTypes.shape({
                id: PropTypes.number,
                skillName: PropTypes.string,
            })
        ),
    }).isRequired,
    likedPostIds: PropTypes.instanceOf(Set).isRequired,
    handlePostClick: PropTypes.func.isRequired,
    handlePostLike: PropTypes.func.isRequired,
};

export default PostCard;
