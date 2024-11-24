import axios from "axios";



export const createAssignment = async (credentials) => {
    const response = await axios.post('/api/assignments/createAssignment', credentials);
    return response;
}

export const getAssignmentsForStudent = async (studentID, token) => {
    const response = await axios.get(`/api/assignments/displayAssignments/${studentID}`, {
        headers : {
            Authorization : `Bearer ${token}`
        },
    });
    // return response.data;

    return(

      [
        {
            "id": 6,
            "title": "English Literature Analysis",
            "description": "Write a detailed analysis of the themes explored in 'Pride and Prejudice,' focusing on the depiction of love, social expectations, and class distinctions. Include a close reading of key passages to support your argument and ensure your essay is well-structured and clearly written.",
            "dueDate": "2024-11-28",
            "document": "literature_analysis_guide.pdf",
            "uploadedDocument": null,
            "status": "PENDING",
            "subject": "English Literature",
            "grade": null
        },
        {
            "id": 7,
            "title": "Physics Lab Report",
            "description": "Complete a detailed lab report based on the pendulum experiment, explaining the principles of simple harmonic motion. Include data tables, calculations of the period of oscillation for different lengths of pendulums, and an analysis of the relationship between length and period.",
            "dueDate": "2024-12-03",
            "document": "physics_lab_instructions.pdf",
            "uploadedDocument": "pendulum_lab_report.pdf",
            "status": "GRADED",
            "subject": "Physics",
            "grade": 87
        },
        {
            "id": 8,
            "title": "Biology Field Study",
            "description": "Document your observations during the biology field trip, focusing on the biodiversity in the area. Identify and record various plant and insect species, and provide descriptions of their habitats and ecological significance. Include photographs if possible to enhance the visual documentation.",
            "dueDate": "2024-12-07",
            "document": null,
            "uploadedDocument": null,
            "status": "PENDING",
            "subject": "Biology",
            "grade": null
        },
        {
            "id": 9,
            "title": "Economics Case Study",
            "description": "Research and analyze the impact of inflation on local businesses in your area. Discuss the causes of inflation, its effect on production costs, pricing strategies, and the overall business environment. Use real-world data and credible sources to support your conclusions.",
            "dueDate": "2024-12-20",
            "document": "economics_case_study_guidelines.pdf",
            "uploadedDocument": "inflation_case_study.pdf",
            "status": "SUBMITTED",
            "subject": "Economics",
            "grade": null
        },
        {
            "id": 10,
            "title": "Chemistry Practical",
            "description": "Perform a titration experiment to determine the concentration of an unknown acid. Ensure to carefully measure the volume of the titrant used and record the observations accurately. Provide a detailed report with all necessary calculations and conclusions based on your findings.",
            "dueDate": "2024-12-01",
            "document": "titration_experiment.pdf",
            "uploadedDocument": null,
            "status": "PENDING",
            "subject": "Chemistry",
            "grade": null
        },
        {
            "id": 11,
            "title": "Music Composition",
            "description": "Compose an original 2-minute piece of music using a piano. The piece should demonstrate your understanding of musical structure, harmony, and rhythm. Ensure to include at least two different chord progressions and explore a range of musical dynamics and tempos.",
            "dueDate": "2024-12-10",
            "document": "music_composition_tips.pdf",
            "uploadedDocument": "original_composition.mp3",
            "status": "GRADED",
            "subject": "Music",
            "grade": null
        },
        {
            "id": 12,
            "title": "Geography Map Project",
            "description": "Create a detailed map showing the different climate zones of the world. Ensure the map is clear, with an appropriate legend and color-coding to distinguish between the various zones. Include relevant geographical features such as mountains, rivers, and oceans to provide context.",
            "dueDate": "2024-12-12",
            "document": "map_project_instructions.pdf",
            "uploadedDocument": null,
            "status": "PENDING",
            "subject": "Geography",
            "grade": null
        },
        {
            "id": 13,
            "title": "Philosophy Debate Preparation",
            "description": "Prepare for a debate on the concept of determinism. Research and develop strong arguments for both sides of the debate, citing at least two major philosophical texts. Your preparation should include a detailed analysis of key concepts such as free will, causality, and moral responsibility.",
            "dueDate": "2024-12-08",
            "document": null,
            "uploadedDocument": "determinism_arguments.pdf",
            "status": "SUBMITTED",
            "subject": "Philosophy",
            "grade": null
        },
        {
            "id": 14,
            "title": "Physical Education Performance Test",
            "description": "Demonstrate proficiency in a series of athletic skills as part of your physical education performance test. This includes a timed run, strength exercises, and agility drills. Ensure to bring appropriate athletic gear and be prepared for both indoor and outdoor activities.",
            "dueDate": "2024-11-29",
            "document": "performance_test_details.pdf",
            "uploadedDocument": null,
            "status": "PENDING",
            "subject": "Physical Education",
            "grade": null
        },
        {
            "id": 15,
            "title": "Graphic Design Portfolio",
            "description": "Create a portfolio showcasing your best graphic design work. The portfolio should include a variety of designs that demonstrate your skills in layout, typography, color theory, and visual communication. The final submission should be in PDF format and include at least five different design projects.",
            "dueDate": "2024-12-15",
            "document": "portfolio_guidelines.pdf",
            "uploadedDocument": "design_portfolio.pdf",
            "status": "GRADED",
            "subject": "Graphic Design",
            "grade": 88
        }
    ]
    );
}


export default { createAssignment, getAssignmentsForStudent };