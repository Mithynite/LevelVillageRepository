import React from "react";
import Select from "react-select";

const PostSearchFilter = ({
                              searchQuery,
                              onSearchChange,
                              selectedSkills,
                              onSkillsChange,
                              dateOrder,
                              onDateOrderChange,
                              allSkills,
                          }) => {
    return (
        <div className="search-filter-container">
            <div className="search-bar">
                <input
                    type="text"
                    placeholder="Search posts..."
                    value={searchQuery}
                    onChange={onSearchChange}
                />
            </div>

            <div className="filters-bar">
                <div className="filter-group">
                    <label htmlFor="skillFilter">Filter by Skills:</label>
                    <Select
                        isMulti
                        options={allSkills.map(skill => ({
                            value: skill.id,
                            label: skill.skillName,
                        }))}
                        value={allSkills
                            .filter(skill => selectedSkills.includes(skill.id))
                            .map(skill => ({value: skill.id, label: skill.skillName}))}
                        onChange={(selectedOptions) =>
                            onSkillsChange(selectedOptions.map(option => option.value))
                        }
                        className="skill-select-dropdown"
                        classNamePrefix="react-select"
                        styles={{
                            control: (base) => ({
                                ...base,
                                minWidth: "200px",
                                backgroundColor: "rgba(30, 27, 22, 1)",
                                border: "2px solid goldenrod",
                                color: "white",
                                borderRadius: "5px",
                                padding: "5px",
                                boxShadow: "none",
                                "&:hover": {
                                    borderColor: "white",
                                },
                            }),
                            menu: (base) => ({
                                ...base,
                                backgroundColor: "rgba(30, 27, 22, 1)",
                                border: "1px solid goldenrod",
                                zIndex: 20,
                            }),
                            option: (base, state) => ({
                                ...base,
                                backgroundColor: state.isFocused
                                    ? "rgba(255, 215, 0, 0.2)"
                                    : "rgba(30, 27, 22, 1)",
                                color: state.isSelected ? "goldenrod" : "white",
                                "&:hover": {
                                    backgroundColor: "rgba(255, 215, 0, 0.5)",
                                },
                            }),
                            multiValue: (base) => ({
                                ...base,
                                backgroundColor: "goldenrod",
                                color: "black",
                                borderRadius: "4px",
                            }),
                            multiValueLabel: (base) => ({
                                ...base,
                                color: "black",
                            }),
                            multiValueRemove: (base) => ({
                                ...base,
                                color: "black",
                                "&:hover": {
                                    backgroundColor: "rgba(255, 215, 0, 0.5)",
                                    color: "white",
                                },
                            }),
                        }}
                    />
                </div>

                <div className="filter-group">
                    <label htmlFor="dateOrder">Sort by Date:</label>
                    <Select
                        options={[
                            {value: "recent", label: "Most Recent"},
                            {value: "oldest", label: "Oldest First"}
                        ]}
                        value={{value: dateOrder, label: dateOrder === "recent" ? "Most Recent" : "Oldest First"}}
                        onChange={(selectedOption) => onDateOrderChange(selectedOption.value)}
                        className="date-select-dropdown"
                        classNamePrefix="react-select"
                        styles={{
                            control: (base) => ({
                                ...base,
                                backgroundColor: "rgba(30, 27, 22, 1)",
                                border: "2px solid goldenrod",
                                color: "white",
                                borderRadius: "5px",
                                padding: "5px",
                                boxShadow: "none",
                                "&:hover": {
                                    borderColor: "white",
                                },
                            }),
                            menu: (base) => ({
                                ...base,
                                backgroundColor: "rgba(30, 27, 22, 1)",
                                border: "1px solid goldenrod",
                                zIndex: 20,
                            }),
                            option: (base, state) => ({
                                ...base,
                                backgroundColor: state.isFocused
                                    ? "rgba(255, 215, 0, 0.2)"
                                    : "rgba(30, 27, 22, 1)",
                                color: state.isSelected ? "goldenrod" : "white",
                                "&:hover": {
                                    backgroundColor: "rgba(255, 215, 0, 0.5)",
                                },
                            }),
                            singleValue: (base) => ({
                                ...base,
                                color: "white",
                            }),
                        }}
                    />

                </div>
            </div>
        </div>
    );
};

export default PostSearchFilter;
